inThisBuild(Seq(
  version          := "0.2.0-SNAPSHOT",
  organization     := "com.abdulradi",
  organizationName := "nullable",

  scalaVersion     := "3.0.0-RC1",
  scalacOptions ++= Seq(
      "-Ykind-projector",
      "-Yexplicit-nulls",
      "-source", "future",
  ),

  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.6" % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.3" % Test
  ),
))

lazy val core = (project in file("core"))
  .settings(name := "nullable-core")

lazy val root = (project in file("."))
  .settings(name := "nullable")

ThisBuild / description := "Makes nullable values as easy to deal with as scala.Option without the allocation cost"
ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/tabdulradi/nullable"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/tabdulradi/nullable"),
    "scm:git@github.com:tabdulradi/nullable.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "tabdulradi",
    name  = "Tamer Abdulradi",
    email = "tamer@abdulradi.com",
    url   = url("http://abdulradi.com")
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
