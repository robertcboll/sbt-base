import sbt._
import Keys._

object Build extends sbt.Build {

  override lazy val settings = super.settings ++
    Seq(
      organization := "sh.robb",
      name := "sbt-base"
    )

  import com.typesafe.sbt.SbtGit._

  lazy val root = Project(id = "sbt-base", base = file("."))
    .settings(versionWithGit: _*)
    .settings(git.baseVersion in ThisBuild := "git")
    .settings(sbtPlugin := true)
    .settings(Dependencies.plugins: _*)


  object Dependencies {

    lazy val plugins = Seq(
      addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4"), 
      addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1"), 
      addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1"), 
      addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.5"), 
      addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6"), 
      addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2"),
      addSbtPlugin("net.databinder.giter8" % "giter8-scaffold" % "0.6.4")  
    )
  }
}

