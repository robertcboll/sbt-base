package sbtbase

import sbt._
import Keys._


object Templates {

  import ToolSettings._

  def RootProject(id: String, deps: ClasspathDep[ProjectReference]*): Project = {
    Project(id = id, base = file("."))
      .settings(Projects.root: _*)
      .dependsOn(deps.toList: _*)
  }

  def DocProject(id: String,
                 deps: ClasspathDep[ProjectReference]*): Project = {
    Project(id = id, base = file(id))
      .dependsOn(deps.toList: _*)
      .settings(Docs.docs: _*)
  }

  def LangProject(id: String,
                  lang: Lang.Language,
                  deps: ClasspathDep[ProjectReference]*): Project = {

    Project(id = id, base = file(id))
      .dependsOn(deps.toList: _*)
      .configs(IntegrationTest)
      .settings(lang.settings: _*)
      .settings(Tests.all(lang): _*)
  }

  def JavaProject(id: String,
                  deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Lang.Java, deps: _*)
  }

  def ScalaProject(id: String,
                   deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Lang.Scala, deps: _*)
  }


  object Lang {

    trait Language {
      def settings: Seq[Def.Setting[_]]
      def testlib(scope: Configuration = Test): Seq[Def.Setting[Seq[ModuleID]]]
    }

    object Java extends Language {
      override def settings = {
        Seq(
          autoScalaLibrary := false,
          crossPaths := false
          //javacOptions in (Compile,doc) += "-Xdoclint:none"
        ) ++ checkstyle ++ findbugs
      }

      override def testlib(scope: Configuration = Test) = Seq(
        libraryDependencies ++= Seq(
          "com.novocode" % "junit-interface" % "0.11" % scope,
          "pl.pragmatists" % "JUnitParams" % "1.0.3" % scope
        )
      )
    }

    object Scala extends Language {
      override def settings = {
        Seq(
          scalacOptions := Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlint")
        )
      }

      override def testlib(scope: Configuration = Test) = Seq(
        libraryDependencies ++= Seq(
          "org.scalatest" %% "scalatest" % "2.2.2" % scope
        )
      )
    }
  }


  private object Projects {

    import com.typesafe.sbt.SbtGit._

    lazy val root = Seq(
      publishArtifact := false,
      publish := {},
      publishLocal := {},
      publishTo := Some(Resolver.file("devnull", file("target/devnull"))),
      showCurrentGitBranch,
      ToolSettings.writeVersion,
      ToolSettings.writeDpkg,
      ToolSettings.dpkgVersion
    ) ++ versionWithGit
  }


  private object Docs {

    import com.typesafe.sbt.SbtSite.SiteKeys._
    import com.typesafe.sbt.SbtSite.site

    import sbtunidoc.Plugin.UnidocKeys._
    import sbtunidoc.{Plugin => Unidoc}

    lazy val docs =
      Unidoc.scalaJavaUnidocSettings ++
      site.settings ++ site.pamfletSupport() ++ site.publishSite() ++
      site.addMappingsToSiteDir(mappings in(Unidoc.ScalaUnidoc, packageDoc), "latest/scaladoc") ++
      site.addMappingsToSiteDir(mappings in(Unidoc.JavaUnidoc, packageDoc), "latest/javadoc") ++
      Seq(
        crossPaths := false,
        publishArtifact in Compile := false,
        doc in Compile <<= (doc in Compile).dependsOn(unidoc in Compile),
        doc in Compile <<= (doc in Compile).dependsOn(makeSite in Compile)
      )
  }


  private object Tests {

    def all(lang: Lang.Language) = it(lang) ++ unit(lang) ++ coverage ++ Seq(
      testOptions in ThisBuild <+= (target in Test) map {
        t => sbt.Tests.Argument("-o", "-u", s"$t/test-reports")
      }
    )

    def unit(lang: Lang.Language) = lang.testlib(Test)

    def it(lang: Lang.Language) = Defaults.itSettings ++ lang.testlib(IntegrationTest)

    import de.johoop.jacoco4sbt.JacocoPlugin._

    lazy val coverage = jacoco.settings ++ itJacoco.settings
  }
}

