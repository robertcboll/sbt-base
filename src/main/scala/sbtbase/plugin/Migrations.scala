package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  object Keys {
    val migrate = InputKey[Unit]("migrate", "Perform database migrations")
    val environment = SettingKey[Option[String]]("environment", "The migrations environment.")
    val Migration = config("migrations")
  }

  import Keys._

  lazy val migrations = inConfig(Migration)(Defaults.configSettings) ++ Seq(
    ivyConfigurations += Migration,

    version in migrate := "3.2.2-SNAPSHOT",
    environment in migrate := sys.props.get("migrate.env"),
    mainClass in migrate := Some("org.apache.ibatis.migration.Migrator"),

    fullClasspath in migrate <<= fullClasspath in Migration,

    libraryDependencies += "org.mybatis" % "mybatis-migrations" % (version in migrate).value % Migration,

    migrate := {
      val main = (mainClass in migrate).value getOrElse ""
      val base = (baseDirectory in migrate).value
      val cp = (fullClasspath in migrate).value

      val env = (environment in migrate).value
      
      val input: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val args: Seq[String] = input

      val mig = new Migrator(streams.value.log, outputStrategy.value, base.getPath, cp.files, main)

      if (!env.isEmpty) mig.run(args ++ Seq(s"--env=${env.get}"))        
      else mig.run(args)
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

