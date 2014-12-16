package sbtbase

import sbt._
import Keys._

object Tests {

  import Langs._

  def all(lang: Language) = tests(lang, Test) ++ 
                                 tests(lang, IntegrationTest) ++ 
                                 Defaults.itSettings ++ coverage

  def tests(lang: Language, scope: Configuration) = {
    lang match {
      case Java => Seq(
        libraryDependencies ++= Seq(
          "com.novocode" % "junit-interface" % "0.11" % scope,
          "pl.pragmatists" % "JUnitParams" % "1.0.3" % scope
        ),
        testOptions <+= (target in scope) map {
          t => sbt.Tests.Argument(TestFrameworks.JUnit, "-v")
        }
      )
      case Scala => Seq(
        libraryDependencies ++= Seq(
          "org.scalatest" %% "scalatest" % "2.2.2" % scope
        ),
        testOptions <+= (target in scope) map {
          t => sbt.Tests.Argument("-oD", "-u", s"$t/test-reports")
        }
      )
    }
  }
     
  import de.johoop.jacoco4sbt.JacocoPlugin._
  lazy val coverage = jacoco.settings ++ itJacoco.settings
}

