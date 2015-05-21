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
  def getRequests: List[Request] = requests
}

// Mr Dummy Consumer simply shouts to the log the messages it receives
class StatsActor extends Actor with ActorLogging {
  var sessions: List[SessionHistory] = List.empty

  def receive: Receive = {
    case reqs: List[Request] => sessions = sessions :+ SessionHistory(reqs)
    case message => log.debug(s"Stats has received: $message")
  }

  /**
   * Statistics calulations
   */
  def calculateRequestsPerBrowser(sessions: List[SessionHistory]): Map[String, Int] = {
    val count = for {browser <- Session.browsers} yield browser ->
                                      sessions.flatMap(sessionHistory => sessionHistory.getRequests).
                                        count(req => req.browser == browser)
    count.toMap
  }

  def calculatePageVisitPercentage(sessions: List[SessionHistory]): Map[String,Int] = {
    val visits: Map[String,Int] = calculateRequestsPerBrowser(sessions)
    val totalVisits: Int = visits.map(el => el._2).sum
    visits.map(element => (element._1 -> (element._2*100 / totalVisits)))
  }

  def top2browsers(sessions: List[SessionHistory]): List[String] = {
    var browsers = calculateRequestsPerBrowser(sessions)
    var top2 = List.empty[String]
      top2 = top2 :+ browsers.maxBy(_._2)._1

    browsers = browsers.filter(_._1 != top2.head)
    top2 = top2 :+ browsers.maxBy(_._2)._1
    top2
  }

  def top2referrers(sessions: List[SessionHistory]): List[String] = {
    val countedReferrers = for {referrer <- Session.referrers} yield referrer ->
                                sessions.flatMap(sessionHistory => sessionHistory.getRequests).
                                  count(req => req.referrer == referrer)
    var referrers = countedReferrers.toMap

    var top2 = List.empty[String]
    top2 = top2 :+ referrers.maxBy(_._2)._1

    referrers = referrers.filter(_._1 != top2.head)
    top2 = top2 :+ referrers.maxBy(_._2)._1
    top2
  }

  def top3landingPages(sessions: List[SessionHistory]): List[String] = {
    var landingPages = for {url <- Session.urls} yield url ->
      sessions.flatMap(sessionHistory => sessionHistory.getRequests).
        count(req => req.url == url)

    var top3 = List.empty[String]

    for(i <- 0 to 2){
      top3 = top3 :+ landingPages.maxBy(_._2)._1
      landingPages = landingPages.filter(_._1 != top3(i))
    }

    top3
  }


  def calculateBusiestMinute(reqs: List[Request]): BusiestMinute = {
    val busiest = reqs.map(req => req.copy(timestamp = req.timestamp / (1000 * 60))).
      groupBy(req => req.timestamp)
    val record = busiest.maxBy(_._2.size)
    BusiestMinute(record._1, record._2.size)
  }

  /**
   * Calculates average visit time for each url.
   * Note that sink pages automatically have a visit time of 0.
   */
  def calculateVisitTimePerURL(reqs: List[SessionHistory]): Map[String, Long] = {
    val listOfVisits = reqs.map(ses => Visit.fromRequests(ses.getRequests)).flatten
    val mapByUrl = listOfVisits.groupBy(visit => visit.request.url)
    mapByUrl.map(entry =>
      entry._1 -> entry._2.foldLeft(0L)((acc, vis) => acc + vis.duration) / entry._2.size
    )
  }
}
