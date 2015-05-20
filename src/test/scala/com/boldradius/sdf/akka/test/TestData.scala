package com.boldradius.sdf.akka.test

import com.boldradius.sdf.akka.{Session, Request}


object TestData {
  var requests = Vector(Request(1, 1, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(2, 2, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(3, 3, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(4, 4, Session.urls(0), Session.referrers(0), Session.browsers(0)),
                        Request(5, 5, Session.urls(0), Session.referrers(0), Session.browsers(0)))

}
