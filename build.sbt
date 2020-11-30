name := "sdk-source-testing"
organization := "io.github.synesso"
scalaVersion := "2.13.4"
resolvers ++= Seq(
  "jitpack" at "https://jitpack.io",
  Resolver.jcenterRepo
)

libraryDependencies ++= List(
  "com.github.synesso" %% "scala-stellar-sdk" % "0.17.0",
  "org.specs2" %% "specs2-core" % "4.7.0" % "test"
)
