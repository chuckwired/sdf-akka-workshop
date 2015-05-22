package com.boldradius.sdf.akka

import akka.actor._
import com.boldradius.sdf.akka.LiveStatsActor._

object LiveStatsActor {
  def props = Props[LiveStatsActor]

  // Messaging protocol
  case class CurrentStats(users: Int)
  case object UpdateLiveStats
}

// Keeps track of the LIVE statistics
class LiveStatsActor extends Actor with ActorLogging {

  var currentStats: LiveStatistics = LiveStatistics(0, Map.empty, Map.empty)

  def receive: Receive = {
    case newStats: LiveStatistics =>
      currentStats = newStats
      printStats()
    case message =>
      log.debug(s"Stats has received: $message")
  }

  def printStats(): Unit = {
    println("===========================")
    println("LIVE Stats:")
    println("___________________________")
    println(s"Current users: ${currentStats.users}")
    println(s"Browser info: ${currentStats.usersPerBrowser}")
    println(s"Url info: ${currentStats.usersPerUrl}")
  }
}
