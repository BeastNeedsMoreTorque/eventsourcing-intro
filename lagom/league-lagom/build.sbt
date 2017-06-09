organization in ThisBuild := "eu.reactivesystems"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.11"


lazy val `league-lagom` = (project in file("."))
  .aggregate(`league-api`, `league-impl`)

lazy val `league-api` = (project in file("league-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `league-impl` = (project in file("league-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      "mysql" % "mysql-connector-java" % "6.0.6",
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
      "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`league-api`)

scalacOptions in ThisBuild :=  Seq("-unchecked", 
          "-deprecation",
          "-encoding", "UTF-8",
          "-feature",                
          "-unchecked",
          "-Xlint",
          "-Yno-adapted-args",       
          "-Ywarn-dead-code", 
          "-Ywarn-numeric-widen",   
          "-Ywarn-value-discard",
          "-Ywarn-unused-import")

lagomCassandraCleanOnStart in ThisBuild := true
