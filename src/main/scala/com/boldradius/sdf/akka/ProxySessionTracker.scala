package com.boldradius.sdf.akka

import akka.actor._
import com.boldradius.sdf.akka.ProxySessionTracker.CheckRequestsCountPerSecond
import com.boldradius.sdf.akka.RequestConsumer.Reject
import com.boldradius.sdf.akka.SessionTracker.{DeathMessage, CheckIfRunHelpChat, CheckSessionActivity}

import scala.concurrent.duration.FiniteDuration

/**
 * Created by robertsliwinski on 22/05/15.
 */

object ProxySessionTracker {
  def props(rcActor: ActorRef, statsActor: ActorRef, sessionTimeout: FiniteDuration, sessionId: Long = 1): Props =
    Props(new ProxySessionTracker(rcActor, statsActor, sessionTimeout, sessionId))

  case class CheckRequestsCountPerSecond(reqCount: Int)
}


case class ProxySessionTracker(rcActor: ActorRef, statsActor: ActorRef, sessionTimeout: FiniteDuration, sessionId: Long) extends Actor with ActorLogging {
  import context.dispatcher

  var requestsCount = 0
  val sessionTracker = context.actorOf(SessionTracker.props(statsActor, sessionTimeout, sessionId), "pst-" + sessionId.toString)

  override def receive: Receive = {
    case req: Request =>
      requestsCount = requestsCount+1
      context.system.scheduler.scheduleOnce(FiniteDuration(1, "seconds"), self, CheckRequestsCountPerSecond(requestsCount))
      sessionTracker forward req
    case CheckRequestsCountPerSecond(oldRequestsCount) =>
      if(requestsCount-10 > oldRequestsCount){
        println("SENDING REJECT")
        rcActor ! Reject
      }
  }
}
