package sbtbase

import sbt._
import Keys._

trait Language {

  def settings: Seq[Def.Setting[_]]
  def tests: Seq[Def.Setting[_]]
}

object Langs {

  object Java extends Language {
    override def settings = Settings.settings(Java)
    override def tests = Tests.settings(Java)
  }

  object Scala extends Language {
    override def settings = Settings.settings(Scala)
    override def tests = Tests.settings(Scala)
  }
}

