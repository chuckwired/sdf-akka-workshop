package com.boldradius.sdf.akka.test

import com.boldradius.sdf.akka.SessionTracker
import akka.testkit.TestActorRef

class SessionTrackerSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in SessionTracker with 0 Request element after we send a String" in {
      val sessionTracker = TestActorRef(SessionTracker.props)
      sessionTracker.receive("TestString")
      val st: SessionTracker = sessionTracker.underlyingActor
      st.requests.length shouldBe 0
    }

    "result in SessionTracker with 1 Request element" in {
      val sessionTracker = TestActorRef(SessionTracker.props)
      sessionTracker.receive(TestData.requests(0))
      val st: SessionTracker = sessionTracker.underlyingActor
      st.requests.length shouldBe 1
    }
  }
}
