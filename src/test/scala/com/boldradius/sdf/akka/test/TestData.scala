package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka.{SessionHistory, StatsActor, Session, Request}

import scala.concurrent.duration.FiniteDuration


object TestData {

  val requests = List(Request(1, 1432197362, Session.urls(4), "google", "chrome"),
                        Request(2, 1432117378, Session.urls(1), "google", "chrome"),
                        Request(3, 1432197389, Session.urls(2), "facebook", "firefox"),
                        Request(4, 1432127433, Session.urls(1), "google", "ie"),
                        Request(5, 1432127433, Session.urls(4), "twitter", "chrome"),
                        Request(6, 1432127433, Session.urls(4), "twitter", "firefox"))

  val sessions = List(SessionHistory(requests),SessionHistory(requests),SessionHistory(requests))


  val testSessionTimeout = FiniteDuration(2, TimeUnit.SECONDS)
}



