import sbt._

object Dependencies {

  // val squerylVersion = "0.9.6-RC2"

  val squeryl      = "org.squeryl" %% "squeryl" % "0.9.6-RC2"
  val h2Db         = "com.h2database" % "h2" % "1.3.173"
  val mysql        = "mysql" % "mysql-connector-java" % "5.1.26"
  val postgresql   = "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"  // jdbc41 if jdk 1.7+

  val buildDeps = Seq(squeryl, h2Db)
}