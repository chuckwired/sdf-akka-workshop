package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit

import com.boldradius.sdf.akka.{Session, Request}

import scala.concurrent.duration.FiniteDuration


object TestData {
  val requests = List(Request(1, 1, Session.urls(4), Session.referrers(8), Session.browsers(5)),
                        Request(2, 2, Session.urls(1), Session.referrers(1), Session.browsers(3)),
                        Request(3, 3, Session.urls(2), Session.referrers(2), Session.browsers(2)),
                        Request(4, 4, Session.urls(1), Session.referrers(1), Session.browsers(3)),
                        Request(5, 5, Session.urls(4), Session.referrers(8), Session.browsers(5)))

  val testSessionTimeout = FiniteDuration(2, TimeUnit.SECONDS)
}
