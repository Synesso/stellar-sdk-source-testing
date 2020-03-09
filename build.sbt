name := "sdk-source-testing"
organization := "io.github.synesso"
scalaVersion := "2.13.1"
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= List(
  "com.github.synesso" %% "scala-stellar-sdk" % "0.11.1",
  "org.specs2" %% "specs2-core" % "4.7.0" % "test"
)
