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
      val requestsPerBrowser = statsActor.calculateRequestsPerBrowser(sessions)
      requestsPerBrowser("chrome") shouldBe 21
      requestsPerBrowser("firefox") shouldBe 14
      requestsPerBrowser("ie") shouldBe 7
    }

    "calculate visits percentage per browser" in new StatsActorSetup {
      val visistsPercentage = statsActor.calculatePageVisitPercentage(sessions)
      visistsPercentage("chrome") shouldBe 50
      visistsPercentage("firefox") shouldBe 33
      visistsPercentage("ie") shouldBe 16
    }

    "calculate top2 browsers" in new StatsActorSetup {
      val top2browsers = statsActor.top2browsers(sessions)
      top2browsers(0) shouldBe "chrome"
      top2browsers(1) shouldBe "firefox"
    }

    "calculate top2 referrers" in new StatsActorSetup {
      val top2referrers = statsActor.top2referrers(sessions)
      top2referrers(0) shouldBe "google"
      top2referrers(1) shouldBe "twitter"
    }

    "calculate the busiest minute" in new StatsActorSetup {
      val busiestMinute = statsActor.calculateBusiestMinute(List(sessions.head))
      busiestMinute.minute shouldBe 23868
    }

    "calculate top3 landing pages" in new StatsActorSetup {
      val top3landingPages = statsActor.top3landingPages(sessions)
      top3landingPages(0) shouldBe "/store"
      top3landingPages(1) shouldBe "/"
      top3landingPages(2) shouldBe "/about"
    }

    "calculate top3 sink pages" in new StatsActorSetup {
      val top3sinkPages = statsActor.top3sinkPages(sessions)
      top3sinkPages(0) shouldBe "/about"
      top3sinkPages(1) shouldBe "/"
      top3sinkPages(2) shouldBe "/store"
    }

    "create Visit correctly" in {
      Visit.fromRequests(requests1).last shouldEqual Visit.fromRequests(List(requests1.last)).last
    }

    "calculate average visit time per url" in new StatsActorSetup {
      val averagePerUrl = statsActor.calculateVisitTimePerURL(sessions)
      averagePerUrl shouldEqual Map("/about" -> 1984003, "/" -> 1167060, "/t" -> 0, "/store" -> 2092128)
    }


  }
}
