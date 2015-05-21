package com.boldradius.sdf.akka.test

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka._
import com.boldradius.sdf.akka.test.TestData._


class StatsActorSpec extends BaseAkkaSpec {
  "The StatsActor" should {
    "result in RequestConsumer with 1 sessionStorage element" in {
      val statsActor = TestActorRef(StatsActor.props)
      val st: StatsActor = statsActor.underlyingActor
      val requestsPerBrowser = st.calculateRequestsPerBrowser(TestData.requests)
      val visistsPercentage = st.calculatePageVisitPercentage(TestData.requests)
      var top2browsers = st.top2browsers(TestData.requests)
      var top2referrers = st.top2referrers(TestData.requests)
      val requestsPerBrowser = st.calculateRequestsPerBrowser(requests)

      val a = TestData.requests

      println(requestsPerBrowser)
      println(visistsPercentage)
//      rc.sessionStorage.size shouldBe 1
    }

    "calculate the busiest minute" in {
      val statsActor = TestActorRef(StatsActor.props)
      val st: StatsActor = statsActor.underlyingActor
      val busiestMinute = st.calculateBusiestMinute(requests)
      busiestMinute.minute shouldBe 23868
    }

  }
}
