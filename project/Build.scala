import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "cidadao-server"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
	"mysql" % "mysql-connector-java" % "5.1.18",
  "securesocial" %% "securesocial" % "master-SNAPSHOT" exclude("org.scala-stm", "scala-stm_2.10.0"),
  "com.codahale" % "jerkson_2.9.1" % "0.5.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
