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
  "securesocial" %% "securesocial" % "2.1.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
  )

}
