package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")

  val migrations = Seq(
    baseDirectory in migrate := file("migrations"),

    migrate := {
      val path = (baseDirectory in migrate).value

      val args: Array[String] = Def.spaceDelimited("<arg>").parsed.toArray
      val added: Array[String] = Array("-p", path.getPath)
      val fullargs: Array[String] = added ++ args

      import com.ondeck.migrations.cli._

      CommandLine.main(fullargs)
    }
  )
}

