package com.boldradius.sdf.akka

import akka.actor._

object EmailActor {
  def props = Props[EmailActor]
}

// Simulate an external e-mail sender
class EmailActor extends Actor with ActorLogging {

  def receive: Receive = {
    case StatsActor.StatsActorError =>
      log.error("TO admin@app.com: BRO the StatsActor can't be automatically resuscitated, fix it!")
  }
}
