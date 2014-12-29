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

  lazy val native = RootProject(
    uri("https://github.com/sbt/sbt-native-packager.git#ae7c5d5f488607ce6b5a9adad0db3fe0b535df0f"))
  
  lazy val giter8 = ProjectRef(
    uri("https://github.com/n8han/giter8.git#7e18719d6b8504f0c0ada1c8931725142e9054fb"), "giter8-scaffold")

  lazy val root = Project(id = "sbt-base", base = file("."))
    .settings(sbtPlugin := true)
    .settings(Dependencies.plugins: _*)
    .dependsOn(native, giter8)

  object Dependencies {

    lazy val plugins = Seq(
      addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2"),

      addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4"),

      /* docs and site */
      addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1"),
      addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1"),

      /* packaging */
      //addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0-M3"),

      /* analysis tools */
      addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6"),
      addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.3.0"), // not java8 compatible
      addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "0.4.1") 
    )
  }
}

