package sbtbase

import sbt._
import Keys._

trait Language {

  def settings: Seq[Def.Setting[_]]
  def tests: Seq[Def.Setting[_]]
}

object Langs {

  object Java extends Language {
    override def settings = Settings.all(Java)
    override def tests = Tests.all(Java)
  }

  object Scala extends Language {
    override def settings = Settings.all(Scala)
    override def tests = Tests.all(Scala)
  }
}

