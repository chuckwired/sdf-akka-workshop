package com.boldradius.sdf.akka.test

import akka.testkit.TestActorRef
import com.boldradius.sdf.akka.StatsActor

/**
 * Created by charlesrice on 21/05/15.
 */
trait StatsActorSetup {
  val st = TestActorRef(StatsActor.props)
  val statsActor: StatsActor = st.underlyingActor
}
