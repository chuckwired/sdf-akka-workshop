package com.boldradius.sdf.akka

import java.util.concurrent.TimeUnit

import akka.actor._
import com.boldradius.sdf.akka.SessionTracker._

import scala.concurrent.duration.FiniteDuration

object SessionTracker {
  def props(statsActor: ActorRef, sessionTimeout: FiniteDuration, sessionId: Long = 1): Props =
    Props(new SessionTracker(statsActor, sessionTimeout, sessionId))

  /**
   * Messaging protocol
   */
  case class CheckSessionActivity(requestCount: Int)

  case class DeathMessage(sessionId: Long)

  case class CheckIfRunHelpChat()

}

class SessionTracker(statsActor: ActorRef, sessionTimeout: FiniteDuration, sessionId: Long) extends Actor with ActorLogging {

  import context.dispatcher

  // List of Requests made by within a unique session ID
  var requests = List.empty[Request]
  var myTimer: Option[Cancellable] = None

  override def receive: Receive = {
    case x: Request =>
      myTimer.map(timer => timer.cancel())
      requests = requests :+ x
      myTimer = Some(context.system.scheduler.scheduleOnce(sessionTimeout, self, CheckSessionActivity(requests.size)))
    case CheckSessionActivity(oldRequestSize) =>
      if (requests.size == oldRequestSize){
        // Send requests to be aggregated
        statsActor ! StatsActor.SendRequests(requests)
        myTimer.map(timer => timer.cancel())
        // Tell the request consumer that you've died
        if(requests.size>0)
          context.parent ! DeathMessage(requests.head.sessionId)
        // Suicide1
        context.stop(self)
      }
  }
}
