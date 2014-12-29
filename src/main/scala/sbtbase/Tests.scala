package sbtbase

import sbt._
import Keys._

object Tests {

  object Keys {
    lazy val AcceptanceTest = config("at").extend(Test)
    lazy val AllTests = "it,at,test"
  }

  import Keys.AcceptanceTest

  import Langs.{Java, Scala}

  def settings(lang: Language) = tests(lang, Test) ++ 
                            tests(lang, IntegrationTest) ++
                            tests(lang, AcceptanceTest) ++
                            inConfig(AcceptanceTest)(Defaults.testSettings) ++
                            Defaults.itSettings ++ coverage

  def tests(lang: Language, scope: Configuration) = {
    lang match {
      case Java => Seq(
        libraryDependencies ++= Seq(
          "com.novocode" % "junit-interface" % "0.11" % scope
        ),
        testOptions <+= (target in scope) map {
          t => sbt.Tests.Argument(TestFrameworks.JUnit, "-v")
        }
      )
      case Scala => Seq(
        testOptions <+= (target in scope) map {
          t => sbt.Tests.Argument("-oD", "-u", s"$t/test-reports")
        }
      )
    }
  }
     
  import de.johoop.jacoco4sbt.{JacocoPlugin => Jacoco}
  import Jacoco.{jacoco, itJacoco}

  lazy val coverage = jacoco.settings ++ itJacoco.settings
}

