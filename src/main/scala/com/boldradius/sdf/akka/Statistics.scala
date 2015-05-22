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
