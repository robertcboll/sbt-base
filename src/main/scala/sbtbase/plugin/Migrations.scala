package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  object Keys {
    val migr8 = InputKey[Unit]("migr8", "database migrations")
    val Migration = config("migrations")
  }

  import sbtbase.Configs.Keys._
  import Keys._

  lazy val migrations = inConfig(Migration)(Defaults.configSettings) ++ Seq(
    ivyConfigurations += Migration,

    version in migr8 := "3.2.0",
    mainClass in migr8 := Some("org.apache.ibatis.migration.Migrator"),

    fullClasspath in migr8 <<= fullClasspath in Migration,

    libraryDependencies += "org.mybatis" % "mybatis-migrations" % (version in migr8).value % Migration,

    migr8 := {
      val main = (mainClass in migr8).value getOrElse ""
      val base = (baseDirectory in migr8).value
      val cp = (fullClasspath in migr8).value

      val environment = env.value      
      
      val input: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val args: Seq[String] = input

      val mig = new Migrator(streams.value.log, outputStrategy.value, base.getPath, cp.files, main)

      if (!environment.isEmpty) mig.run(args ++ Seq(s"--env=${environment.get}"))
      else mig.run(args)
      println()
    }
  )

  class Migrator(log: Logger, 
                  outputStrategy: Option[OutputStrategy], 
                  basedir: String, 
                  classpath: Seq[File], 
                  main: String) {

    def run(args: Seq[String]): Unit = {

      val dir = ensure(basedir)
      val opts = ForkOptions(
        outputStrategy = outputStrategy,
        bootJars = classpath,
        workingDirectory = dir
      )

      run(opts, args, main)
    }

    private[this] def run(opts: ForkOptions, args: Seq[String], main: String) = {
      Fork.java.fork(opts, Seq[String](main) ++ args).exitValue()
    }

    private[this] def ensure(path: String): Option[File] = {
      file(path).mkdirs
      Some(file(path))
    }
  }
}

