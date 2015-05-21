package com.boldradius.sdf.akka

import akka.actor._
import com.boldradius.sdf.akka.StatsActor.BusiestMinute

object StatsActor {
  def props = Props[StatsActor]

  /**
   *  Statistics protocol
   */
  case class BusiestMinute(minute: Long, numberOfRequests: Int)
}

case class SessionHistory(requests: List[Request]) {
  def getRequests = requests
}

// Mr Dummy Consumer simply shouts to the log the messages it receives
class StatsActor extends Actor with ActorLogging {
  var sessions: List[SessionHistory] = List.empty

  def receive: Receive = {
    case reqs: List[Request] => sessions = sessions :+ SessionHistory(reqs)
    case message => log.debug(s"Stats has received: $message")
  }

  def calculateRequestsPerBrowser(reqs: List[Request]): Map[String, Int] = {
    val count = for {browser <- Session.browsers} yield browser -> reqs.count(req => req.browser == browser)
    count.toMap
  }

  def calculatePageVisitPercentage(reqs: List[Request]): Map[String,Int] = {
    val visits: Map[String,Int] = calculateRequestsPerBrowser(reqs)
    val totalVisits: Int = visits.map(el => el._2).sum
    visits.map(element => (element._1 -> (element._2*100 / totalVisits)))
  }

  def top2browsers(reqs: List[Request]): List[String] = {
    var browsers = calculateRequestsPerBrowser(reqs)
    var top2 = List.empty[String]
      top2 = top2 :+ browsers.maxBy(_._2)._1

    browsers = browsers.filter(_._1 != top2.head)
    top2 = top2 :+ browsers.maxBy(_._2)._1
    top2
  }

  def top2referrers(reqs: List[Request]): List[String] = {
    val countedReferrers = for {referrer <- Session.referrers} yield referrer -> reqs.count(req => req.referrer == referrer)
    var referrers = countedReferrers.toMap

    var top2 = List.empty[String]
    top2 = top2 :+ referrers.maxBy(_._2)._1

    referrers = referrers.filter(_._1 != top2.head)
    top2 = top2 :+ referrers.maxBy(_._2)._1
    top2
  }

  def calculateBusiestMinute(reqs: List[Request]): BusiestMinute = {
    val busiest = reqs.map(req => req.copy(timestamp = req.timestamp / (1000 * 60))).
      groupBy(req => req.timestamp)
    val record = busiest.maxBy(_._2.size)
    BusiestMinute(record._1, record._2.size)
  }

  def calculateVisitTimePerURL(reqs: List[Request]) = {
    reqs.groupBy(req => req.url)
  }
}
