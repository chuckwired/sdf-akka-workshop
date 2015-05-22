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

  var currentNumberOfUsers = 0

  def receive: Receive = {
    case CurrentStats(users) =>
      currentNumberOfUsers = users
      printStats()
    case message =>
      log.debug(s"Stats has received: $message")
  }

  def printStats(): Unit = {
    println("===========================")
    println("LIVE Stats:")
    println("___________________________")
    println(s"Current users: $currentNumberOfUsers")
  }
}
