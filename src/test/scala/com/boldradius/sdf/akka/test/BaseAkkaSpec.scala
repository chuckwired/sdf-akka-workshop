package com.boldradius.sdf.akka.test

import akka.actor.{ ActorIdentity, ActorRef, ActorSystem, Identify }
import akka.testkit.{TestProbe}
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration.{ DurationInt, FiniteDuration }

abstract class BaseAkkaSpec extends BaseSpec with BeforeAndAfterAll {

  implicit class TestProbeOps(probe: TestProbe) {

    def expectActor(path: String, max: FiniteDuration = probe.remaining): ActorRef = {
      probe.within(max) {
        var actor = null: ActorRef
        probe.awaitAssert {
          (probe.system actorSelection path).tell(Identify(path), probe.ref)
          probe.expectMsgPF(100 milliseconds) {
            case ActorIdentity(`path`, Some(a)) => actor = a
          }
        }
        actor
      }
    }
  }

  implicit val system = ActorSystem()

  override protected def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination()
  }
}

