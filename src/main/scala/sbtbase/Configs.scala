package sbtbase

import sbt._
import Keys._

object Configs {

  object Keys {
    val env = SettingKey[Option[String]]("env", "environment key")
    val localEnv = SettingKey[Option[String]]("local", "local(fallback) environment key")
  }

  import Keys._
 
  lazy val settings = {
    Seq(
      localEnv := Some("local"),
      env := sys.props.get("config.env") orElse localEnv.value
    )
  }
}

