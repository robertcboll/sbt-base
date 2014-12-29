import sbt._
import Keys._


object Build extends sbt.Build {

  import com.typesafe.sbt.SbtGit._
  import GitKeys._

  override lazy val settings = super.settings ++ versionWithGit ++ 
    Seq(
      organization := "sbtbase",
      name := "sbt-base",
      publishMavenStyle := false,
      scalacOptions := Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlint")
    )

  lazy val root = Project(id = "sbt-base", base = file("."))
    .settings(sbtPlugin := true)
    .settings(dependencyOverrides += "org.clapper" %% "scalasti" % "2.0.0")
    .settings(Dependencies.plugins: _*)
    .dependsOn(RootProject(uri("https://github.com/robertcboll/sbt-native-packager.git#1.0.0-rb")))

  object Dependencies {

    lazy val plugins = Seq(
      addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2"),

      addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4"),
      addSbtPlugin("net.databinder.giter8" % "giter8-scaffold" % "0.6.6"),

      /* docs and site */
      addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1"),
      addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1"),

      /* packaging */
      //addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7-3bae1fd61adc96e124577c939779edef129bb9dd"),

      /* analysis tools */
      addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6"),
      addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.3.0"), // not java8 compatible
      addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "0.4.1")
    
      /* need to override scalasti to 2.0 to avoid asm conflict with findbugs */
    )
  }
}

