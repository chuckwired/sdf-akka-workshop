package com.boldradius.sdf.akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import com.boldradius.sdf.akka.SessionTracker.CheckSessionActivity

import scala.concurrent.duration.FiniteDuration

object SessionTracker {
  def props(statsActor: ActorRef, sessionTimeout: FiniteDuration): Props = Props(new SessionTracker(statsActor, sessionTimeout))

  /**
   * Messaging protocol
   */
  case class CheckSessionActivity(requestCount: Int)
}

class SessionTracker(statsActor: ActorRef, sessionTimeout: FiniteDuration) extends Actor with ActorLogging {

  import context.dispatcher

  // List of Requests made by within a unique session ID
  var requests = List.empty[Request]

  override def receive: Receive = {
    case x: Request =>
      requests = requests :+ x
      context.system.scheduler.scheduleOnce(sessionTimeout, self, CheckSessionActivity(requests.size))
    case CheckSessionActivity(oldRequestSize) =>
      if (requests.size == oldRequestSize){
        statsActor ! requests
        context.stop(self)
      }
  }
}
