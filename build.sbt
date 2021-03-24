inThisBuild(Seq(
  organization     := "com.abdulradi",
  organizationName := "nullable",
  description      := "Makes nullable values as easy to deal with as scala.Option without the allocation cost",
  licenses         := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage         := Some(url("https://github.com/tabdulradi/nullable")),
  scmInfo          := Some(ScmInfo(url("https://github.com/tabdulradi/nullable"), "scm:git@github.com:tabdulradi/nullable.git")),
  developers       := List(
    Developer(
      id    = "tabdulradi",
      name  = "Tamer Abdulradi",
      email = "tamer@abdulradi.com",
      url   = url("http://abdulradi.com")
    )
  ),

  githubWorkflowTargetTags ++= Seq("v*"),
  githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )
  ),

  scalaVersion := "3.0.0-RC1",
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
  .aggregate(core)
  .settings(
    name := "nullable",
    skip in publish := true
  )
