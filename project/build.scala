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
      resolvers += Resolver.mavenLocal
    )


  lazy val root = Project(id = "sbt-base", base = file("."))
    .settings(sbtPlugin := true)
    .settings(Dependencies.plugins: _*)
    .settings(dependencyOverrides += "org.clapper" %% "scalasti" % "2.0.0")

  object Dependencies {

    lazy val plugins = Seq(
      addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2"),

      addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4"),
      //addSbtPlugin("net.databinder.giter8" % "giter8-scaffold" % "0.6.6"),

      addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1"),
      addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1"),

      addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0-RC2"),

      addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6"),
      addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.3.0"), // not java8 compatible
      addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "0.4.1")
    )
  }
}

