package io.steeltoe.sbt

import sbt.Keys._
import sbt._


object Templates {

  def RootProject(id: String, deps: Seq[ClasspathDep[ProjectReference]] = Seq.empty): Project = {
    Project(id = id, base = file("."))
      .settings(Projects.root: _*)
  }

  def JavaProject(id: String, base: Option[File] = None,
                  deps: Seq[ClasspathDep[ProjectReference]] = Seq.empty): Project = {
    Project(id = id, base = base getOrElse file(id))
      .configs(IntegrationTest)
      .settings(Tests.all(Tests.Java): _*)
      .settings(Projects.java: _*)
  }

  def ScalaProject(id: String, base: Option[File] = None,
                   deps: Seq[ClasspathDep[ProjectReference]] = Seq.empty): Project = {
    Project(id = id, base = base getOrElse file(id))
      .configs(IntegrationTest)
      .settings(Tests.all(Tests.Scala): _*)
      .settings(Projects.scala: _*)
  }

  def DocProject(id: String, base: Option[File] = None,
                 deps: Seq[ClasspathDep[ProjectReference]] = Seq.empty): Project = {
    Project(id = id, base = base getOrElse file(id))
      .settings(Docs.docs: _*)
  }

  /* deployments */
  lazy val runnable = Deployments.runnable
  lazy val targz = Deployments.targz


  private object Projects {

    import com.typesafe.sbt.SbtGit._

    lazy val root = Seq(
      publishArtifact := false,
      publishLocal := {},
      publishTo := Some(Resolver.file("devnull", file("/dev/null"))),
      showCurrentGitBranch
    ) ++ versionWithGit


    lazy val java = Seq(
      autoScalaLibrary := false,
      crossPaths := false
    )


    lazy val scala = Seq(
      scalacOptions := Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlint")
    )
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

  private object Deployments {

    import spray.revolver.RevolverPlugin._

    lazy val runnable = Revolver.settings ++
      Seq(fork := true)


    import com.typesafe.sbt.SbtNativePackager.NativePackagerKeys._
    import com.typesafe.sbt.SbtNativePackager._

    val gentgz = TaskKey[sbt.File]("distro", "Generate the targz distributino")
    lazy val targz = packageArchetype.java_application ++
      addArtifact(artifact in(Compile, gentgz), gentgz).settings ++
      Seq(
        gentgz <<= (normalizedName, version, baseDirectory) map { (name, version, target) =>
          target / "target" / "universal" / (name + "-" + version + ".tgz")
        },
        artifact in(Compile, gentgz) := {
          val previous: Artifact = (artifact in(Compile, gentgz)).value
          previous.copy(`type` = "dist", extension = "tgz", classifier = Some("dist"))
        },
        dist <<= packageZipTarball in Universal
      )
  }

  private object Tests {

    trait Language {
      def lib(scope: Configuration = Test): Seq[Def.Setting[Seq[ModuleID]]]
    }

    object Java extends Language {
      override def lib(scope: Configuration = Test) = Seq(
        libraryDependencies ++= Seq(
          "com.novocode" % "junit-interface" % "0.11" % scope,
          "pl.pragmatists" % "JUnitParams" % "1.0.3" % scope
        )
      )
    }

    object Scala extends Language {
      override def lib(scope: Configuration = Test) = Seq(
        libraryDependencies ++= Seq(
          "org.scalatest" %% "scalatest" % "2.2.2" % scope
        )
      )
    }


    def all(lang: Language) = it(lang) ++ unit(lang) ++ coverage

    def unit(lang: Language) = lang.lib(Test)

    def it(lang: Language) = Defaults.itSettings ++ lang.lib(IntegrationTest)

    lazy val coverage = de.johoop.jacoco4sbt.JacocoPlugin.jacoco.settings
  }
}

