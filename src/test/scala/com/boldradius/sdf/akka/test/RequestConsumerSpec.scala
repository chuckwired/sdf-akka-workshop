package com.boldradius.sdf.akka.test

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka._
//import com.boldradius.sdf.akka.{SessionTracker, RequestConsumer}

class RequestConsumerSpec extends BaseAkkaSpec {
  "Sending Request" should {
    "result in RequestConsumer with 1 sessionStorage element" in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.requests(0))
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage.size shouldBe 1
    }

    """result in RequestConsumer with 1 sessionStorage with an Actor named "st-1" """ in {
      val requestConsumer = TestActorRef(RequestConsumer.props)
      requestConsumer.receive(TestData.requests(0))
      val rc: RequestConsumer = requestConsumer.underlyingActor
      rc.sessionStorage(1).path.name shouldBe "st-" + 1
    }
  }
}
