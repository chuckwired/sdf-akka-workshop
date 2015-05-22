package com.boldradius.sdf.akka
import java.io._
import akka.actor._

import com.boldradius.sdf.akka.StatsActor._
import scala.concurrent.duration.FiniteDuration
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import Statistics._

object StatsActor {
  def props = Props[StatsActor]

  case object SaveStatistics
  case class SendRequests(regs: List[Request])
  /**
   *  Statistics protocol
   */
  case object StatsActorError extends IllegalStateException("An artificial error occured.")
}

case class SessionHistory(requests: List[Request]) {
  def getRequests: List[Request] = requests
}

// Collects and calculates lots of different statistics
class StatsActor extends Actor with ActorLogging {
  var sessions: List[SessionHistory] = List.empty
  var statistics: JsValue = loadStatistics()
//  loadStatistics()
  self ! SaveStatistics

  def receive: Receive = {
    case SendRequests(reqs) => sessions = sessions :+ SessionHistory(reqs)

    case StatsActorError => throw StatsActorError

    case SaveStatistics =>
            if(sessions.size>0)
              saveStatistics(sessions)
            context.system.scheduler.scheduleOnce(FiniteDuration(30, "seconds"), self, SaveStatistics)

    case message => log.debug(s"Stats has received: $message")
  }

  def saveStatistics(sessions: List[SessionHistory]) = {
    val statistics = Json.obj("requestsPerBrowser" -> SessionStatistics.calculateRequestsPerBrowser(sessions),
            "visitedPagesByPercentage" -> SessionStatistics.calculatePageVisitPercentage(sessions),
            "visitsTimePerUrl" -> SessionStatistics.calculateVisitTimePerURL(sessions),
            "busiestMinutes" -> SessionStatistics.calculateBusiestMinute(sessions).toJson,
            "top2browsers" -> SessionStatistics.calculateTop2browsers(sessions),
            "top2referrers" -> SessionStatistics.calculateTop2referrers(sessions),
            "top3landingPages" -> SessionStatistics.calculateTop3landingPages(sessions),
            "top3sinkPages" -> SessionStatistics.calculateTop3sinkPages(sessions))

    new File("statistics.txt" ).delete()
    val pw = new PrintWriter(new File("statistics.txt" ))
    pw.write(statistics.toString())
    pw.close
  }

  def loadStatistics(): JsValue = {
    val fileName = "statistics.txt"
    if(new File(fileName).exists()) {
      Json.parse(Source.fromFile("statistics.txt").mkString)
    }
    else{
      Json.parse("")
    }
  }
}
