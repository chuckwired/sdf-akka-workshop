package com.boldradius.sdf.akka.test

import akka.actor.ActorRef
import akka.testkit.{TestProbe, TestActorRef}
import com.boldradius.sdf.akka._
import shapeless._

import scala.collection.mutable.{Map => MutableMap}

//import com.boldradius.sdf.akka.{SessionTracker, RequestConsumer}

class RequestConsumerSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in RequestConsumer with 1 sessionStorage element" in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.sessions(0).getRequests.head)
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage.size shouldBe 1
    }

    """result in RequestConsumer with 1 sessionStorage with an Actor named "st-1" """ in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.sessions(0).getRequests.head)
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage(1).actorRef.path.name shouldBe "st-" + 1
    }

    """use the lens to update the session storage properly""" in {
      val requestConsumer: RequestConsumer = TestActorRef(RequestConsumer.props).underlyingActor

      val sessionStorage = MutableMap.empty[Long, SessionTrackerMetaData]
      val lastUrlLens = lens[SessionTrackerMetaData] >> 'lastUrl

      val testData = SessionTrackerMetaData(TestProbe().ref, "chrome", "previousUrl")

      sessionStorage.update(23L, testData)

      sessionStorage(23L) shouldEqual testData

      sessionStorage.update(23L, lastUrlLens.set(sessionStorage(23L))("newUrl"))

      sessionStorage(23L) shouldEqual testData.copy(lastUrl = "newUrl")
    }
  }
}
