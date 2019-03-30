inThisBuild(Seq(
  scalaVersion     := "2.12.8",
  version          := "0.1.0-SNAPSHOT",
  organization     := "com.abdulradi",
  organizationName := "nullable",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
  )
))

lazy val core = (project in file("core"))
  .settings(name := "nullable-core")

lazy val root = (project in file("."))
  .settings(name := "nullable")
