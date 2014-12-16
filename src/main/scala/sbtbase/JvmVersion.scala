package sbtbase

import sbt._
import Keys._

object JvmVersion {

  def settings(jvmVersion: String): Seq[Def.Setting[_]] = {
    val common = Seq(
      scalacOptions in (Compile, compile) += s"-target:jvm-$jvmVersion",
      javacOptions in (Compile, compile) ++= Seq("-source", jvmVersion, "-target", jvmVersion)
    )
    
    val runtimeVersion = sys.props.get("java.version")
    println("the runtimeVersion is " + runtimeVersion)
    
    runtimeVersion match {
      case Some(vers) =>
        vers match {
          case v if v.startsWith("1.7") => 
            println("setting to common only")
            common
          case v if v.startsWith("1.8") => 
            println("setting to doclint plus common")
            common ++ Seq(javacOptions in (Compile, doc) += "-Xdoclint:none")
        }
      case _ => common
    }
  }
}

