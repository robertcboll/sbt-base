package sbtbase

import sbt._
import Keys._

object Settings {

  import Langs._

  def common = {
    Tools.style ++ Tools.findbugs
  }

  def all(lang: Language) = {
    lang match {
      case Java => Seq(
        autoScalaLibrary := false,
        crossPaths := false,
        javacOptions in (Compile, compile) ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
      )
      case Scala => Seq(
        scalacOptions := Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlint")
      )
    }
  }
}

