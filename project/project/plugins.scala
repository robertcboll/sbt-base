import sbt._
import Keys._

object PluginDef extends Build {
  
  override def projects = Seq(root)

  lazy val root = Project("plugins", file(".")) dependsOn 
    RootProject(uri("https://github.com/robertcboll/giter8.git#v0.6.7"))
}

