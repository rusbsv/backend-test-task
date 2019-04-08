name := """backend-test-task"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.47",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.slick" %% "slick" % "3.3.0",
  evolutions, 
  jdbc,
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
)
