package sbtbase

import sbt._
import Keys._

object JvmVersion {

  def settings(jvmVersion: String): Seq[Def.Setting[_]] = {
    val common = Seq(
      scalacOptions in (Compile, compile) += s"-target:jvm-$jvmVersion",
      javacOptions in (Compile, compile) ++= Seq("-source", jvmVersion, "-target", jvmVersion)
    )
    
    sys.props.get("java.version") match {
      case Some(vers) =>
        vers match {
          case v if v.startsWith("1.7") => common
          case v if v.startsWith("1.8") => common ++ Seq(javacOptions in (Compile, doc) += "-Xdoclint:none")
        }
      case _ => common
    }
  }
}

