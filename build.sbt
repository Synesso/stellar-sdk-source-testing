name := "sdk-source-testing"
organization := "io.github.synesso"
scalaVersion := "2.12.9"
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= List(
  "com.github.synesso" %% "scala-stellar-sdk" % "0.10.1",
  "org.specs2" %% "specs2-core" % "4.7.0" % "test"
)
