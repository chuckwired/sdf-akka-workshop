package com.boldradius.sdf.akka

import java.util.concurrent.TimeUnit

import akka.actor._
import com.boldradius.sdf.akka.Request

import scala.concurrent.duration.{Duration, FiniteDuration}

object RequestConsumer {
  def props = Props[RequestConsumer]
}

// Primary consumer of the requests
class RequestConsumer extends Actor with ActorLogging {
  // TODO: Remove session from sessionStorage when it dies.
  var sessionStorage = Map.empty[Long, ActorRef]
  val statsActor = context.actorOf(StatsActor.props)

  def receive: Receive = {
    case Request(id, now, randomUrl, referrer, browser) if sessionStorage.exists(record => record._1 == id) =>
      sessionStorage(id) forward Request(id, now, randomUrl, referrer, browser)
    case Request(id, now, randomUrl, referrer, browser) =>
      val stActor = makeSessionActor(id)
      sessionStorage += (id -> stActor)
      stActor forward Request(id, now, randomUrl, referrer, browser)
    case message => log.debug(s"Received the following message: $message")
  }

  //Factory method to create a new session actor
  def makeSessionActor(id: Long): ActorRef = {
    val timeout: FiniteDuration = Duration(context.system.settings.config.getDuration("akka-workshop.session-tracker.session-timeout", TimeUnit.SECONDS), TimeUnit.SECONDS)
    context.actorOf(SessionTracker.props(statsActor, timeout), "st-" + id.toString)
  }
}
