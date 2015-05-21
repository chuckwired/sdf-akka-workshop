package com.boldradius.sdf.akka.test

import java.util.concurrent.TimeUnit

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka.{SessionHistory, StatsActor, Session, Request}

import scala.concurrent.duration.FiniteDuration


object TestData {

  val requests1 = List(Request(1, 1432197362, "/store", "google", "chrome"),
                        Request(1, 1432117378, "/store", "google", "chrome"),
                        Request(1, 1432197389, "/store", "facebook", "firefox"),
                        Request(1, 1432127433, "/", "google", "ie"),
                        Request(1, 1432127433, "/", "twitter", "chrome"),
                        Request(1, 1432127433, "/about", "twitter", "firefox"))

  val requests2 = List(Request(2, 1432197362, "/store", "google", "chrome"),
                        Request(2, 1432117378, "/store", "google", "chrome"),
                        Request(2, 1432197389, "/store", "facebook", "firefox"),
                        Request(2, 1432127433, "/", "google", "ie"),
                        Request(2, 1432127433, "/", "twitter", "chrome"),
                        Request(2, 1432127433, "/about", "twitter", "firefox"))

  val requests3 = List(Request(3, 1432197362, "/store", "google", "chrome"),
                        Request(3, 1432117378, "/store", "google", "chrome"),
                        Request(3, 1432197389, "/store", "facebook", "firefox"),
                        Request(3, 1432127433, "/", "google", "ie"),
                        Request(3, 1432127433, "/", "twitter", "chrome"),
                        Request(3, 1432127433, "/about", "twitter", "firefox"))

  val requests4 = List(Request(4, 1432197362, "/", "google", "chrome"),
                        Request(4, 1432117378, "/store", "google", "chrome"),
                        Request(4, 1432197389, "/store", "facebook", "firefox"),
                        Request(4, 1432127433, "/", "google", "ie"),
                        Request(4, 1432127433, "/", "twitter", "chrome"),
                        Request(4, 1432127433, "/", "twitter", "firefox"))

  val requests5 = List(Request(5, 1432197362, "/", "google", "chrome"),
                        Request(5, 1432117378, "/store", "google", "chrome"),
                        Request(5, 1432197389, "/store", "facebook", "firefox"),
                        Request(5, 1432127433, "/", "google", "ie"),
                        Request(5, 1432127433, "/", "twitter", "chrome"),
                        Request(5, 1432127433, "/", "twitter", "firefox"))

  val requests6 = List(Request(6, 1432197362, "/about", "google", "chrome"),
                        Request(6, 1432117378, "/store", "google", "chrome"),
                        Request(6, 1432197389, "/store", "facebook", "firefox"),
                        Request(6, 1432127433, "/", "google", "ie"),
                        Request(6, 1432127433, "/", "twitter", "chrome"),
                        Request(6, 1432127433, "/store", "twitter", "firefox"))

//  var requests2 = (requests1.head.copy(url="/store") :: requests1.tail.reverse.tail.reverse) :+ requests1.last.copy(url="/about")
//  var requests3 = (requests1.head.copy(url="/store") :: requests1.tail.reverse.tail.reverse) :+ requests1.last.copy(url="/about")
//  var requests4 = (requests1.head.copy(url="/") :: requests1.tail.reverse.tail.reverse) :+ requests1.last.copy(url="/")
//  var requests5 = (requests1.head.copy(url="/") :: requests1.tail.reverse.tail.reverse) :+ requests1.last.copy(url="/")
//  var requests6 = (requests1.head.copy(url="/about") :: requests1.tail.reverse.tail.reverse) :+ requests1.last.copy(url="/store")



  val sessions = List(SessionHistory(requests1),SessionHistory(requests2),SessionHistory(requests3),SessionHistory(requests3)
                  ,SessionHistory(requests4),SessionHistory(requests5),SessionHistory(requests6))


  val testSessionTimeout = FiniteDuration(2, TimeUnit.SECONDS)
}



