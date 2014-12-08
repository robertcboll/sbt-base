package sbtbase.plugin

import sbt._
import Keys._


object Migrations extends Plugin {

  lazy val Migration = config("migrations")
  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")

  val migrations = Seq(
    baseDirectory in migrate := file("migrations"),
    managedClasspath in migrate := Classpaths.managedJars(Migration, Set("jar"), update.value),
    ivyConfigurations += Migration,
    libraryDependencies += "org.mybatis" % "mybatis-migrations" % "3.2.2-ONDECK" % Migration,

    migrate := {
      val path = (baseDirectory in migrate).value
      val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val cp = (managedClasspath in migrate).value

      Migrator(streams.value, outputStrategy.value, path.getPath, cp)
        .run(args)
    }
  )

  class Migrator(log: Logger, outputStrategy: Option[OutputStrategy], basedir: String, classpath: Seq[File]) {

    val main = "org.apache.ibatis.migration.Migrator"

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
    def apply(streams: TaskStreams, output: Option[OutputStrategy], base: String, cp: Classpath): Migrator = {
      new Migrator(log = streams.log, outputStrategy = output, basedir = base, classpath = cp.files)
    }
  }
}
