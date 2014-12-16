package sbtbase

import sbt._
import Keys._

trait Language {
  import Langs._

  def settings: Seq[Def.Setting[_]]
  def tests: Seq[Def.Setting[_]]
}

object Langs {
  import Settings._
  import Tests._

  object Java extends Language {
    override def settings = Settings.all(Java)
    override def tests = Tests.all(Java)
  }

  object Scala extends Language {
    override def settings = Settings.all(Scala)
    override def tests = Tests.all(Scala)
  }
}

