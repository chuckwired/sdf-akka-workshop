package com.boldradius.sdf.akka
import java.io._
import akka.actor._
import com.boldradius.sdf.akka.StatsActor.{SendRequests, SaveSessions, BusiestMinute}

import scala.concurrent.duration.FiniteDuration
import play.api.libs.json._

object StatsActor {
  def props = Props[StatsActor]

  case object SaveSessions
  case class SendRequests(regs: List[Request])
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

  self ! SaveSessions

  def receive: Receive = {
    case SendRequests(reqs) => sessions = sessions :+ SessionHistory(reqs)
    case message => log.debug(s"Stats has received: $message")
    case SaveSessions => saveSessionsToFile(sessions)
  }


  def saveSessionsToFile(sessions: List[SessionHistory]) = {
    val requestsPerBrowser = calculateRequestsPerBrowser(sessions)
    val visitedPagesByPercentage = calculatePageVisitPercentage(sessions)
    val busiestMinutes = calculateBusiestMinute(sessions(0).getRequests)
    val visitsTimePerUrl = calculateVisitTimePerURL(sessions)
    val top2browsers = calculateTop2browsers(sessions)
    val top2referrers = calculateTop2referrers(sessions)
    val top3landingPages = calculateTop3landingPages(sessions)
    var top3sinkPages = calculateTop3sinkPages(sessions)

    val sessionAsJson = Json.obj("requestsPerBrowser" -> calculateRequestsPerBrowser(sessions),
            "visitedPagesByPercentage" -> calculatePageVisitPercentage(sessions),
            "visitsTimePerUrl" -> calculateVisitTimePerURL(sessions),
            "top2browsers" -> calculateTop2browsers(sessions),
            "top2referrers" -> calculateTop2referrers(sessions),
            "top3landingPages" -> calculateTop3landingPages(sessions),
            "top3sinkPages" -> calculateTop3sinkPages(sessions))

    val pw = new PrintWriter(new File("session.txt" ))
    pw.write(sessionAsJson.toString())
    pw.close
//    context.system.scheduler.scheduleOnce(FiniteDuration(30, "seconds"), self, SaveSessions)
  }


  /**
   * Statistics calulations
   */
  def calculateRequestsPerBrowser(sessions: List[SessionHistory]): Map[String, Int] = {
    val count = for {browser:String <- Session.browsers.toSet} yield browser ->
                    sessions.flatMap(sessionHistory => sessionHistory.getRequests).
                      count(req => req.browser == browser)
    count.toMap
  }

  def calculatePageVisitPercentage(sessions: List[SessionHistory]): Map[String,Int] = {
    val visits: Map[String,Int] = calculateRequestsPerBrowser(sessions)
    val totalVisits: Int = visits.map(el => el._2).sum
    visits.map(element => (element._1 -> (element._2*100 / totalVisits)))
  }

  def calculateTop2browsers(sessions: List[SessionHistory]): List[String] = {
    var browsers = calculateRequestsPerBrowser(sessions)
    var top2 = List.empty[String]
      top2 = top2 :+ browsers.maxBy(_._2)._1

    browsers = browsers.filter(_._1 != top2.head)
    top2 = top2 :+ browsers.maxBy(_._2)._1
    top2
  }

  def calculateTop2referrers(sessions: List[SessionHistory]): List[String] = {
    val countedReferrers = for {referrer:String <- Session.referrers.toSet} yield referrer ->
                               sessions.flatMap(sessionHistory => sessionHistory.getRequests).
                                 count(req => req.referrer == referrer)
    var referrers = countedReferrers.toMap

    var top2 = List.empty[String]
    top2 = top2 :+ referrers.maxBy(_._2)._1

    referrers = referrers.filter(_._1 != top2.head)
    top2 = top2 :+ referrers.maxBy(_._2)._1
    top2
  }

  def calculateTop3landingPages(sessions: List[SessionHistory]): List[String] = {
    var landingPages = for {url:String <- Session.urls.toSet} yield url ->
      sessions.map(sessionHistory => sessionHistory.getRequests.head).
        count(req => req.url == url)

    var top3 = List.empty[String]

    for(i <- 0 to 2){
      top3 = top3 :+ landingPages.maxBy(_._2)._1
      landingPages = landingPages.filter(_._1 != top3(i))
    }

    top3
  }

  def calculateTop3sinkPages(sessions: List[SessionHistory]): List[String] = {
    var sinkPages = for {url:String <- Session.urls.toSet} yield url ->
      sessions.map(sessionHistory => sessionHistory.getRequests.last).
        count(req => req.url == url)

    println(sinkPages)
    var top3 = List.empty[String]

    for(i <- 0 to 2){
      top3 = top3 :+ sinkPages.maxBy(_._2)._1
      sinkPages = sinkPages.filter(_._1 != top3(i))
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
  def calculateVisitTimePerURL(sessions: List[SessionHistory]): Map[String, Long] = {
    val listOfVisits = sessions.map(ses => Visit.fromRequests(ses.getRequests)).flatten
    val mapByUrl = listOfVisits.groupBy(visit => visit.request.url)
    mapByUrl.map(entry =>
      entry._1 -> entry._2.foldLeft(0L)((acc, vis) => acc + vis.duration) / entry._2.size
    )
  }
}
