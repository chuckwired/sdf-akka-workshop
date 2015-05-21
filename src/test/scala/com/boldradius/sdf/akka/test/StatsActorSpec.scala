package com.boldradius.sdf.akka.test

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.boldradius.sdf.akka._
import com.boldradius.sdf.akka.test.TestData._



class StatsActorSpec extends BaseAkkaSpec {
  trait StatsActorSetup {
    val st = TestActorRef(StatsActor.props)
    val statsActor: StatsActor = st.underlyingActor
  }

  "The StatsActor" should {
    "calculate requests per browser" in new StatsActorSetup {
      val requestsPerBrowser = statsActor.calculateRequestsPerBrowser(TestData.requests)
      requestsPerBrowser("chrome") shouldBe 3
      requestsPerBrowser("firefox") shouldBe 2
      requestsPerBrowser("ie") shouldBe 1
    }

    "calculate visits percentage per browser" in new StatsActorSetup {
      val visistsPercentage = statsActor.calculatePageVisitPercentage(TestData.requests)
      visistsPercentage("chrome") shouldBe 50
      visistsPercentage("firefox") shouldBe 33
      visistsPercentage("ie") shouldBe 16
    }

    "calculate top2 browsers" in new StatsActorSetup {
      val top2browsers = statsActor.top2browsers(TestData.requests)
      top2browsers(0) shouldBe "chrome"
      top2browsers(1) shouldBe "firefox"
    }

    "calculate top2 referrers" in new StatsActorSetup {
      val top2referrers = statsActor.top2referrers(TestData.requests)
      top2referrers(0) shouldBe "google"
      top2referrers(1) shouldBe "twitter"
    }

    "calculate the busiest minute" in new StatsActorSetup {
      val busiestMinute = statsActor.calculateBusiestMinute(requests)
      busiestMinute.minute shouldBe 23868
    }

  }
}
