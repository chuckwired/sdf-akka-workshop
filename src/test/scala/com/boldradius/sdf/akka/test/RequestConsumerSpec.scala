package com.boldradius.sdf.akka.test


import akka.actor.ActorRef
import akka.testkit.{TestProbe, TestActorRef}
import com.boldradius.sdf.akka._
import shapeless._

import scala.collection.mutable.{Map => MutableMap}

//import com.boldradius.sdf.akka.{SessionTracker, RequestConsumer}

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka.RequestConsumer.Reject
import com.boldradius.sdf.akka._
import akka.testkit._
import com.boldradius.sdf.akka.test.TestData._



class RequestConsumerSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in RequestConsumer with 1 sessionStorage element" in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.sessions(0).getRequests.head)
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage.size shouldBe 1
    }

    """result in RequestConsumer with 1 sessionStorage with an Actor named "pst-1" """ in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.sessions(0).getRequests.head)
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage(1).actorRef.path.name shouldBe "pst-" + 1
    }

    "result in reject mesage after sending to many request per second" in {
      val rcActor = TestProbe()
      val statsActor = TestProbe()
      val proxySessionTracker = system.actorOf(ProxySessionTracker.props(rcActor.ref, statsActor.ref, testSessionTimeout))

      for(i <- 1 to 15) {
        proxySessionTracker ! TestData.sessions(0).getRequests.head
      }

      Thread.sleep(2000)
      rcActor expectMsg Reject
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
