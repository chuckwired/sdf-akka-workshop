package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit

import com.boldradius.sdf.akka.{Session, Request}

import scala.concurrent.duration.FiniteDuration


object TestData {
  val requests = Vector(Request(1, 1, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(2, 2, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(3, 3, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(4, 4, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(5, 5, Session.urls(0), Session.referrers(0), Session.browsers(0)))

  val testSessionTimeout = FiniteDuration(2, TimeUnit.SECONDS)
}
