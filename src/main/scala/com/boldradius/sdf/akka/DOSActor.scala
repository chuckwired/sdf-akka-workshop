package com.boldradius.sdf.akka

import akka.actor._
import System.{currentTimeMillis => now}
import SessionActor._
import scala.concurrent.duration._

// Wraps around a session and emits requests to the target actor
class DOSActor(target: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  // Upon actor creation, build a new session
  val session = new Session

  // This actor should live as long as possible, sending Clicks every 5 milliseconds
  context.system.scheduler.schedule(5 millis, 5 millis, self, Click)

  override def receive = {
    case Click =>
      // Send a request to the target actor
      val request = session.request
      target ! request
  }
}

object DOSActor {

  def props(target: ActorRef) = Props(new DOSActor(target))

  // Message protocol for the SessionActor
  case object Click
}
