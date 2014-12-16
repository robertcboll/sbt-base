package sbtbase

import sbt._
import Keys._

object Settings {

  import Langs._

  def common = {
    Tools.style ++ Tools.findbugs
  }

  def all(lang: Language) = {
    val common = Tools.style ++ Tools.findbugs

    lang match {
      case Java => common ++ Seq(
        autoScalaLibrary := false,
        crossPaths := false,
        javacOptions in (Compile, compile) ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
      )
      case Scala => common ++ Seq(
        scalacOptions := Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlint")
      )
    }
  }
}

