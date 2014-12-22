package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")
  lazy val Migration = config("migrations")

  val migrations = inConfig(Migration)(Defaults.configSettings) ++ Seq(
    ivyConfigurations += Migration,
    
    baseDirectory in Migration := file("migrations"),
    mainClass in Migration := Some("com.ondeck.migrations.cli.CommandLine"),
    version in Migration := "0.4",

    libraryDependencies += "com.ondeck.migrations" % "migrations-cli" % (version in Migration).value % Migration,

    migrate := {
      java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE)

      val main = (mainClass in Migration).value getOrElse ""
      val path = (baseDirectory in Migration).value
      val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val cp = (fullClasspath in Migration).value

      Migrator(streams.value, outputStrategy.value, path.getPath, cp, main)
        .run(args)
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

  object Migrator {
    def apply(streams: TaskStreams, 
              output: Option[OutputStrategy], 
              base: String, 
              cp: Classpath, 
              main: String): Migrator = {
      new Migrator(log = streams.log, outputStrategy = output, basedir = base, classpath = cp.files, main)
    }
  }
}

