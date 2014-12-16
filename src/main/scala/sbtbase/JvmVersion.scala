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
      case v if jvmVersion.startsWith("1.7") => common
      case v if jvmVersion.startsWith("1.8") => common ++ Seq(
        javacOptions in (Compile, doc) += "-Xdoclint:none"
      )
    }
  }
}

