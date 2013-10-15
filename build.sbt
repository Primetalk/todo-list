import play.Project._
import cloudbees.Plugin._

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

//val main = play.Project(appName, appVersion, appDependencies).settings(cloudBeesSettings :_*).settings(CloudBees.applicationId := Some("your account name/your app name"))
    
CloudBees.deployParams := Map("runtime.java_version" -> "1.7")

