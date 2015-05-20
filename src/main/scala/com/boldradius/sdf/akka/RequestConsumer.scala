package com.boldradius.sdf.akka

import akka.actor._
import com.boldradius.sdf.akka.Request

object RequestConsumer {
  def props = Props[RequestConsumer]
}

// Primary consumer of the requests
class RequestConsumer extends Actor with ActorLogging {
var sessionStorage = Map.empty[Long, ActorRef]

  def receive: Receive = {
    case Request(id, now, randomUrl, referrer, browser) =>
      if(sessionStorage.exists(record => record._1 == id)){
        sessionStorage(id) forward Request(id, now, randomUrl, referrer, browser)
      }
      else {
        val stActor = context.actorOf(SessionTracker.props, "st-" + id.toString)
        sessionStorage += (id -> stActor)
        stActor forward Request(id, now, randomUrl, referrer, browser)
      }
    case message => log.debug(s"Received the following message: $message")
  }
}
