package com.boldradius.sdf.akka.test

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka._


class StatsActorSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in RequestConsumer with 1 sessionStorage element" in {
      val statsActor = TestActorRef(StatsActor.props)
      val st: StatsActor = statsActor.underlyingActor
      val requestsPerBrowser = st.calculateRequestsPerBrowser(TestData.requests)

      println(requestsPerBrowser)
//      rc.sessionStorage.size shouldBe 1
    }
  }
}
