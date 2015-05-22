package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit
import java.io.File

import com.boldradius.sdf.akka.StatsActor.SendRequests
import com.boldradius.sdf.akka.{Request, SessionTracker}
import akka.testkit.{TestKit, TestProbe, TestActorRef}
import akka.testkit.TestKit._
import com.boldradius.sdf.akka.SessionTracker.CheckSessionActivity
import org.scalatest.time._

import scala.concurrent.duration.FiniteDuration
import com.boldradius.sdf.akka.test.TestData._

class SessionTrackerSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in SessionTracker with 0 Request element after we send a String" in {
      val statsActor = TestProbe()

      val sessionTracker = TestActorRef(SessionTracker.props(statsActor.ref, testSessionTimeout))
      sessionTracker.receive("TestString")
      val st: SessionTracker = sessionTracker.underlyingActor
      st.requests.length shouldBe 0
    }

    "result in SessionTracker with 1 Request element" in {
      val statsActor = TestProbe()

      val sessionTracker = TestActorRef(SessionTracker.props(statsActor.ref, testSessionTimeout))
      sessionTracker.receive(sessions(0).getRequests.head)
      val st: SessionTracker = sessionTracker.underlyingActor
      st.requests.length shouldBe 1
    }

    "should forward requests to the statsActor" in {
      val statsActor = TestProbe()

      val sessionTracker = TestActorRef(SessionTracker.props(statsActor.ref, testSessionTimeout))
      sessionTracker.receive(sessions(0).getRequests.head)
      sessionTracker.receive(CheckSessionActivity(1))

      statsActor expectMsg SendRequests(List(sessions(0).getRequests.head))

    }

    "should die if CheckSessionActivity remained the same" in {
      val statsActor = TestProbe()
      val sessionTracker = system.actorOf(SessionTracker.props(statsActor.ref, testSessionTimeout))

      //Watch the sessionTracker
      statsActor.watch(sessionTracker)
      sessionTracker ! CheckSessionActivity(0)

      statsActor expectMsg SendRequests(List())
      statsActor.expectTerminated(sessionTracker)

    }

    "should automatically die" in {
      val statsActor = TestProbe()
      val sessionTracker = system.actorOf(SessionTracker.props(statsActor.ref, testSessionTimeout))

      //Watch the sessionTracker
      statsActor.watch(sessionTracker)
      sessionTracker ! sessions(0).getRequests.head

      new TestKit(system).within(FiniteDuration(1800, "milliseconds"), FiniteDuration(3, "seconds")) {
        val receivedList = statsActor expectMsg SendRequests(List(sessions(0).getRequests.head))
        statsActor.expectTerminated(sessionTracker)
      }
    }

    "wait 10 sec on /help page and start a chat" in {
      val statsActor = TestProbe()

      val sessionTracker = TestActorRef(SessionTracker.props(statsActor.ref, testSessionTimeout))
      sessionTracker.receive(Request(1, 1422197362, "/help", "google", "chrome"))

      new File("chat.log" ).delete()
      new File("chat.log").isFile() shouldBe false
      //TODO check after 10 seconds
    }
  }
}
