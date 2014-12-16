package sbtbase

import sbt._
import Keys._

object JvmVersion {

  def settings(jvmVersion: String): Seq[Def.Setting[_]] = {
    val common = Seq(
      scalacOptions in (Compile, compile) += s"-target:jvm-$jvmVersion",
      javacOptions in (Compile, compile) ++= Seq("-source", jvmVersion, "-target", jvmVersion)
    )

    jvmVersion match {
      case v if jvmVersion.startsWith("1.7") => 
        println("setting to common only")
        common
      case v if jvmVersion.startsWith("1.8") => 
        println("setting to doclint plus common")
        common ++ Seq(
          javacOptions in (Compile, doc) += "-Xdoclint:none"
        )
    }
  }
}

