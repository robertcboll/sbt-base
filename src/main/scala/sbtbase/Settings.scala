package sbtbase

import sbt._
import Keys._

object Settings {

  import Langs.{Java, Scala}

  lazy val common = {
    Tools.style ++ Tools.findbugs
  }

  def settings(lang: Language) = {
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

