package com.boldradius.sdf.akka

import akka.actor._

object RequestConsumer {
  def props = Props[RequestConsumer]
}

// Primary consumer of the requests
class RequestConsumer extends Actor with ActorLogging {

  def receive: Receive = {
    case message => log.debug(s"Received the following message: $message")
  }
}
