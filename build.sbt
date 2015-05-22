name := """RequestProducer"""

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"      % "2.3.9",
  "com.typesafe.akka" %% "akka-testkit"    % "2.3.9" % "test",
  "org.scalatest"     %% "scalatest"       % "2.2.4" % "test",
  "com.typesafe.akka" %% "akka-slf4j"      % "2.3.6",
  "ch.qos.logback"    %  "logback-classic" % "1.1.2",
  "com.typesafe.play" % "play-json_2.11"       % "2.3.9",
  "com.chuusai" %% "shapeless" % "2.1.0"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")

)