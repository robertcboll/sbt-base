package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")
  lazy val Migration = config("migrations")

  val migrations = Seq(
    ivyConfigurations += Migration,
    
    baseDirectory in migrate := file("migrations"),
    mainClass in migrate := Some("com.ondeck.migrations.cli.CommandLine"),

    fullClasspath in migrate <<= fullClasspath in Migration,

    //managedClasspath in migrate := Classpaths.managedJars(Migration, Set("jar"), update.value),

    libraryDependencies += "com.ondeck.migrations" % "migrations-cli" % (version in migrate).value % Migration,

    migrate := {
      java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE)

      val main = (mainClass in migrate).value getOrElse ""
      val path = (baseDirectory in migrate).value
      val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val cp = (managedClasspath in migrate).value

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

