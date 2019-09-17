name := "sdk-source-testing"
organization := "io.github.synesso"
scalaVersion := "2.12.9"
libraryDependencies ++= List(
  "io.github.synesso" %% "scala-stellar-sdk" % "0.9.0",
  "org.specs2" %% "specs2-core" % "4.7.0" % "test"
)
