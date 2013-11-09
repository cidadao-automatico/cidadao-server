/*
// Copyright 2012/2013 de Gustavo Steinberg, Flavio Soares, Pierre Andrews, Gustavo Salazar Torres, Thomaz Abramo
//
// Este arquivo é parte do programa Vigia Político. O projeto Vigia
// Político é um software livre; você pode redistribuí-lo e/ou
// modificá-lo dentro dos termos da GNU Affero General Public License
// como publicada pela Fundação do Software Livre (FSF); na versão 3 da
// Licença. Este programa é distribuído na esperança que possa ser útil,
// mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a
// qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para
// maiores detalhes. Você deve ter recebido uma cópia da GNU Affero
// General Public License, sob o título "LICENCA.txt", junto com este
// programa, se não, acesse http://www.gnu.org/licenses/
*/


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
	"mysql" % "mysql-connector-java" % "5.1.26",
  "securesocial" %% "securesocial" % "2.1.1",
  "nz.ac.waikato.cms.weka" % "weka-stable" % "3.6.9",
  "org.apache.lucene" % "lucene-core" % "4.3.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.3.0",
  "org.apache.mahout" % "mahout-core" % "0.8",
  "org.apache.mahout" % "mahout-math" % "0.8",
  "org.apache.hadoop" % "hadoop-core" % "1.1.2"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    javaOptions in Test += "-Dconfig.file=conf/test.conf"

  )

}
