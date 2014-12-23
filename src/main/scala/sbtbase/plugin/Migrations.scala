package sbtbase.plugin

import sbt._
import Keys._

object Migrations extends Plugin {

  lazy val migrate = InputKey[Unit]("migrate", "Perform database migrations")
  lazy val configFile = SettingKey[Option[String]]("configFile", "The migrations config file.")
  lazy val Migration = config("migrations")

  val migrations = inConfig(Migration)(Defaults.configSettings) ++ Seq(
    ivyConfigurations += Migration,
    
    baseDirectory in migrate := file("migrations"),
    mainClass in migrate := Some("com.ondeck.migrations.cli.CommandLine"),
    version in migrate := "0.4",
    configFile in migrate := Some("migrations.conf"),

    fullClasspath in migrate <<= fullClasspath in Migration,

    libraryDependencies += "com.ondeck.migrations" % "migrations-cli" % (version in migrate).value % Migration,

    migrate := {
      java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE)

      val main = (mainClass in migrate).value getOrElse ""
      val base = (baseDirectory in migrate).value
      val cp = (fullClasspath in migrate).value
      
      val input: Seq[String] = Def.spaceDelimited("<arg>").parsed
      val args: Seq[String] = input ++ Seq("-c", (configFile in migrate).value getOrElse "migrations.conf")
      
      val mig = Migrator(streams.value, outputStrategy.value, base.getPath, cp, main)

      val prop: Option[String] = sys.props.get("config.env")
      if (!prop.isEmpty) mig.run(args ++ Seq("-e", prop.get))        
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

