// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.14"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "com.github.parkdongho"

val tywavesVersion = "0.4.2-SNAPSHOT"
val chiselVersion = "6.4.0"
val scalatestVersion = "3.2.19"

lazy val root = (project in file("."))
  .settings(
    name := "chisel6_env_test",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion,
      "com.github.rameloni" %% "tywaves-chisel-api" % tywavesVersion,
    ),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:reflectiveCalls",
      "-feature",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-Ymacro-annotations",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
  )
