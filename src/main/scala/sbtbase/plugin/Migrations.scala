package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val Migration = config("migrations")
  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")

  val migrations = Seq(
    baseDirectory in migrate := file("migrations"),
    mainClass in migrate := Some("migrations.cli.CommandLine"),
    version in migrate := "0.4",
    managedClasspath in migrate := Classpaths.managedJars(Migration, Set("jar"), update.value),
    ivyConfigurations += Migration,
    libraryDependencies += "com.ondeck.migrations" % "migrations-cli" % (version in migrate).value % Migration,

    migrate := {
      val path = (baseDirectory in migrate).value
      val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val cp = (managedClasspath in migrate).value
      val main = (mainClass in migrate).value getOrElse ""

      Migrator(streams.value, main, outputStrategy.value, path.getPath, cp)
        .run(args)
    }
  )

  class Migrator(log: Logger, main: String, outputStrategy: Option[OutputStrategy], basedir: String, classpath: Seq[File]) {

    def run(args: Seq[String]): Unit = {

      val dir = ensure(basedir)
      val opts = ForkOptions(
        outputStrategy = outputStrategy,
        bootJars = classpath,
        workingDirectory = dir
      )

      run(opts, args)
    }

    private[this] def run(opts: ForkOptions, args: Seq[String]) = {
      Fork.java.fork(opts, Seq[String](main) ++ args).exitValue()
      println() // newline
    }

    private[this] def ensure(path: String): Option[File] = {
      file(path).mkdirs
      Some(file(path))
    }
  }

  object Migrator {
    def apply(streams: TaskStreams, mainClass: String, output: Option[OutputStrategy], base: String, cp: Classpath): Migrator = {
      new Migrator(log = streams.log, main = mainClass, outputStrategy = output, basedir = base, classpath = cp.files)
    }
  }
}

