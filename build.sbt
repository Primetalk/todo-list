import play.Project._

name := """project-arseniy-zhizhelev"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0", 
  "org.webjars" % "bootstrap" % "3.0.0",
  "org.webjars" % "x-editable-bootstrap" % "1.4.6",
  jdbc,
  anorm,
  cache
) 

playScalaSettings
