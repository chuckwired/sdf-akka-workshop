package com.boldradius.sdf.akka

import akka.actor.{Props, ActorLogging, Actor}

object SessionTracker {
  def props: Props = Props(new SessionTracker)
}

class SessionTracker extends Actor with ActorLogging {
  var requests = List.empty[Request]

  override def receive: Receive = {
    case x: Request => requests = requests :+ x
  }
}
