package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit

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
      sessionTracker.receive(requests(0))
      val st: SessionTracker = sessionTracker.underlyingActor
      st.requests.length shouldBe 1
    }

    "should forward requests to the statsActor" in {
      val statsActor = TestProbe()

      val sessionTracker = TestActorRef(SessionTracker.props(statsActor.ref, testSessionTimeout))
      sessionTracker.receive(requests(0))
      sessionTracker.receive(CheckSessionActivity(1))

      statsActor expectMsg List(requests.head)

    }

    "should die if CheckSessionActivity remained the same" in {
      val statsActor = TestProbe()
      val sessionTracker = system.actorOf(SessionTracker.props(statsActor.ref, testSessionTimeout))

      //Watch the sessionTracker
      statsActor.watch(sessionTracker)
      sessionTracker ! CheckSessionActivity(0)

      statsActor expectMsg List()
      statsActor.expectTerminated(sessionTracker)

    }

    "should automatically die" in {
      val statsActor = TestProbe()
      val sessionTracker = system.actorOf(SessionTracker.props(statsActor.ref, testSessionTimeout))

      //Watch the sessionTracker
      statsActor.watch(sessionTracker)
      sessionTracker ! requests.last

//      within(FiniteDuration(2, "seconds"), FiniteDuration(3, "seconds")) {
      new TestKit(system).within(FiniteDuration(1800, "milliseconds"), FiniteDuration(2300, "milliseconds")) {
        statsActor expectMsg List(requests.last)
        statsActor.expectTerminated(sessionTracker)
      }
    }
  }
}
