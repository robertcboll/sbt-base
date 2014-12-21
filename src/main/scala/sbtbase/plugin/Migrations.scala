package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")

  val migrations = Seq(
    baseDirectory in migrate := file("migrations"),
    version in migrate := "0.4",
    libraryDependencies += "com.ondeck.migrations" % "migrations-cli" % (version in migrate).value,

    migrate := {
      val path = (baseDirectory in migrate).value
      val args: Seq[String] = Def.spaceDelimited("<arg>").parsed

      val added: Seq[String] = Seq("", "")
      val fullargs: Seq[String] = added ++ args

      import com.ondeck.migrations.cli._

      CommandLine.main(fullargs.mkString)
    }
  )
}

