package com.boldradius.sdf.akka

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor._
import com.boldradius.sdf.akka.LiveStatsActor.UpdateLiveStats
import scala.concurrent.duration._
import scala.collection.mutable.{Map => MutableMap}

object RequestConsumer {
  def props = Props[RequestConsumer]
}

// Primary consumer of the requests
class RequestConsumer extends Actor with ActorLogging {

  import context.dispatcher
  import shapeless._

  val sessionStorage = MutableMap.empty[Long, SessionTrackerMetaData]
  val statsActor = context.actorOf(StatsActor.props, "stats-service")
  val liveStatsActor = context.actorOf(LiveStatsActor.props, "live-stats-service")

  val lastUrlLens = lens[SessionTrackerMetaData] >> 'lastUrl

  //We want to handle when we fail to restart the statsActor too many times
  context.watch(statsActor)

  context.system.scheduler.schedule(1 seconds, 1 seconds, self, LiveStatsActor.UpdateLiveStats)

  def receive: Receive = {
    case Request(id, now, randomUrl, referrer, browser) if sessionStorage.exists(record => record._1 == id) =>
      // forward request to the session trackers and update the storage's urls
      sessionStorage(id).actorRef forward Request(id, now, randomUrl, referrer, browser)
      sessionStorage.update(id, lastUrlLens.set(sessionStorage(id))(randomUrl))

    case Request(id, now, randomUrl, referrer, browser) =>
      val stActor = makeSessionActor(id)
      sessionStorage += (id -> SessionTrackerMetaData(stActor, browser, randomUrl))
      stActor forward Request(id, now, randomUrl, referrer, browser)

    case SessionTracker.DeathMessage(id) =>
      // When a session has timed out, remove it from the map
      sessionStorage - id

    case UpdateLiveStats =>
      liveStatsActor ! LiveStatistics.calculateStats(sessionStorage.toMap)

    case Terminated(`statsActor`) =>
      val emailActor = context.actorOf(EmailActor.props, "email-service")
      emailActor ! StatsActor.StatsActorError

    case message => log.debug(s"Received the following message: $message")
  }

  //Factory method to create a new session actor
  def makeSessionActor(id: Long): ActorRef = {
    val timeout: FiniteDuration = Duration(context.system.settings.config.getDuration("akka-workshop.session-tracker.session-timeout", TimeUnit.SECONDS), TimeUnit.SECONDS)
    context.actorOf(SessionTracker.props(statsActor, timeout, id), "st-" + id.toString)
  }

  // What to do when children fail, here to catch the StatsActor generally
  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 4) {
      case StatsActor.StatsActorError =>
        println("Got a StatsActorError. Restarting...")
        Restart
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }
}
