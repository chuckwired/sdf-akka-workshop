package com.boldradius.sdf.akka.test

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestProbe, TestKit, TestActorRef}
import com.boldradius.sdf.akka._
import com.boldradius.sdf.akka.test.TestData._
import java.io.File
import scala.concurrent.duration.FiniteDuration


class StatsActorSpec extends BaseAkkaSpec {

  "The StatsActor" should {
    "calculate requests per browser" in new StatsActorSetup {
      val requestsPerBrowser = SessionStatistics.calculateRequestsPerBrowser(sessions)
      requestsPerBrowser("chrome") shouldBe 21
      requestsPerBrowser("firefox") shouldBe 14
      requestsPerBrowser("ie") shouldBe 7
    }

    "calculate visits percentage per browser" in new StatsActorSetup {
      val visistsPercentage = SessionStatistics.calculatePageVisitPercentage(sessions)
      visistsPercentage("chrome") shouldBe 50
      visistsPercentage("firefox") shouldBe 33
      visistsPercentage("ie") shouldBe 16
    }

    "calculate top2 browsers" in new StatsActorSetup {
      val top2browsers = SessionStatistics.calculateTop2browsers(sessions)
      top2browsers(0) shouldBe "chrome"
      top2browsers(1) shouldBe "firefox"
    }

    "calculate top2 referrers" in new StatsActorSetup {
      val top2referrers = SessionStatistics.calculateTop2referrers(sessions)
      top2referrers(0) shouldBe "google"
      top2referrers(1) shouldBe "twitter"
    }

    "calculate the busiest minute" in new StatsActorSetup {
      val busiestMinute = SessionStatistics.calculateBusiestMinute(List(sessions.head))
      busiestMinute.minute shouldBe 23868
    }

    "calculate top3 landing pages" in new StatsActorSetup {
      val top3landingPages = SessionStatistics.calculateTop3landingPages(sessions)
      top3landingPages(0) shouldBe "/store"
      top3landingPages(1) shouldBe "/"
      top3landingPages(2) shouldBe "/about"
    }

    "calculate top3 sink pages" in new StatsActorSetup {
      val top3sinkPages = SessionStatistics.calculateTop3sinkPages(sessions)
      top3sinkPages(0) shouldBe "/about"
      top3sinkPages(1) shouldBe "/"
      top3sinkPages(2) shouldBe "/store"
    }

    "create Visit correctly" in {
      Visit.fromRequests(requests1).last shouldEqual Visit.fromRequests(List(requests1.last)).last
    }

    "calculate average visit time per url" in new StatsActorSetup {
      val averagePerUrl = SessionStatistics.calculateVisitTimePerURL(sessions)
      averagePerUrl shouldEqual Map("/about" -> 1984003, "/" -> 1102224, "/store" -> 2092128)
    }

    "have an email produced when terminated" in {
      val requestConsumer: TestActorRef[RequestConsumer] = TestActorRef(RequestConsumer.props)

      EventFilter.error("TO admin@app.com: BRO the StatsActor can't be automatically resuscitated, fix it!") intercept
        system.stop(requestConsumer.underlyingActor.statsActor)
    }

    "save stats to a file" in new StatsActorSetup{
      statsActor.saveStatistics(sessions)
      new File("statistics.txt").exists shouldBe true
    }

    "load stats from a file and check if there is a content" in new StatsActorSetup{
      statsActor.loadStatistics().toString().length should be > 0
    }

    trait StatsActorSetup {
      val st = TestActorRef(StatsActor.props)
      val statsActor: StatsActor = st.underlyingActor
    }

  }
}
