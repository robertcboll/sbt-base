package sbtbase

import sbt._
import Keys._

object ExtraSettings {

  /* runnable */
  import spray.revolver.RevolverPlugin._

  lazy val runnable = Revolver.settings ++ Seq(
    fork := true,
    ToolSettings.dpkgVersion
  )
}

