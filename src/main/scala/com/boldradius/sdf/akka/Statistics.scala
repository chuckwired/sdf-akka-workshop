package com.boldradius.sdf.akka

import play.api.libs.json._
import shapeless._

/**
 * Model used to contain the statistics of the stats actor
 */

object Statistics {
  val busiestMinuteLens = lens[Statistics] >> 'busiestMinute >> 'minute
}

case class Statistics(
                       requestsPerBrowser: Map[String, Int],
                       busiestMinute: BusiestMinute)

case class BusiestMinute(minute: Long, numberOfRequests: Int) {
  val toJson = JsArray(Seq(JsNumber(minute), JsNumber(numberOfRequests)))
}

/**
 * Model used to calculate and contain the live statistics
 */

object LiveStatistics {

  def calculateUsers() = {
    val stats = Statistics(Map("chrome" -> 5), BusiestMinute(256L, 50))
    val oldUpdated = stats.copy(busiestMinute = stats.busiestMinute.copy(minute = 255L))
    val lensUpdated = Statistics.busiestMinuteLens.set(stats)(23L)
  }

}

case class LiveStatistics(
                           users: Int,
                           usersPerUrl: Map[String, Int],
                           usersPerBrowser: Map[String, Int]
                           )




case object SessionStatistics {

  def calculateRequestsPerBrowser(sessions: List[SessionHistory]): Map[String, Int] = {
    val count = for {browser:String <- Session.browsers.toSet} yield browser ->
      sessions.flatMap(sessionHistory => sessionHistory.getRequests).
        count(req => req.browser == browser)
    count.toMap
  }

  def calculatePageVisitPercentage(sessions: List[SessionHistory]): Map[String,Int] = {
    val visits: Map[String,Int] = calculateRequestsPerBrowser(sessions).filter(el => el._2 > 0)
    val totalVisits: Int = visits.map(el => el._2).sum

    visits.map(element => (element._1 -> (element._2*100 / totalVisits)))
  }

  def calculateTop2browsers(sessions: List[SessionHistory]): List[String] = {
    var browsers = calculateRequestsPerBrowser(sessions)
    var top2 = List.empty[String]

    for(i <- 0 to 1){
      top2 = top2 :+ browsers.maxBy(_._2)._1
      browsers = browsers.filter(_._1 != top2(i))
    }

    top2
  }

  def calculateTop2referrers(sessions: List[SessionHistory]): List[String] = {
    val countedReferrers = for {referrer:String <- Session.referrers.toSet} yield referrer ->
      sessions.flatMap(sessionHistory => sessionHistory.getRequests).
        count(req => req.referrer == referrer)
    var referrers = countedReferrers.toMap

    var top2 = List.empty[String]
    for(i <- 0 to 1){
      top2 = top2 :+ referrers.maxBy(_._2)._1
      referrers = referrers.filter(_._1 != top2(i))
    }

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

    var top3 = List.empty[String]

    for(i <- 0 to 2){
      top3 = top3 :+ sinkPages.maxBy(_._2)._1
      sinkPages = sinkPages.filter(_._1 != top3(i))
    }

    top3
  }


  def calculateBusiestMinute(sessions: List[SessionHistory]): BusiestMinute = {
    val reqs = sessions.flatMap(x => x.getRequests)
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

