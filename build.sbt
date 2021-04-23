inThisBuild(Seq(
  organizationName := "nullable",
  description      := "Makes nullable values as easy to deal with as scala.Option without the allocation cost",
  homepage         := Some(url("https://github.com/tabdulradi/nullable")),
  scmInfo          := Some(ScmInfo(url("https://github.com/tabdulradi/nullable"), "scm:git@github.com:tabdulradi/nullable.git")),

  scalaVersion := "3.0.0-RC3",
  
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.7" % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.3" % Test
  ),
))

lazy val core = (project in file("core"))
  .settings(name := "nullable-core")

lazy val root = (project in file("."))
  .aggregate(core)
  .settings(
    name := "nullable",
    skip in publish := true
  )
