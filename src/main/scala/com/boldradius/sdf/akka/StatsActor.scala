package com.boldradius.sdf.akka

import akka.actor._

object StatsActor {
  def props = Props[StatsActor]
}

// Mr Dummy Consumer simply shouts to the log the messages it receives
class StatsActor extends Actor with ActorLogging {

  def receive: Receive = {
    case message => log.debug(s"Stats has received: $message")
  }
}
