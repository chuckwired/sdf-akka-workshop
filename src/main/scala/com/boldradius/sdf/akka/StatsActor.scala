package com.boldradius.sdf.akka

import akka.actor._

object StatsActor {
  def props = Props[StatsActor]
}

// Mr Dummy Consumer simply shouts to the log the messages it receives
class StatsActor extends Actor with ActorLogging {
  var requests:List[Request] = List.empty[Request]

  def receive: Receive = {
    case reqs: List[Request] => requests = requests ++ reqs
    case message => log.debug(s"Stats has received: $message")
  }

  def calculateRequestsPerBrowser(reqs: List[Request]): Map[String, Int] = {
    val count = for (browser <- Session.browsers) yield browser -> reqs.count(req => req.browser == browser)
    count.toMap
  }
}
