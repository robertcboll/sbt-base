package sbtbase

import sbt._
import Keys._

object Templates {

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

  import Tests.AcceptanceTest

  def LangProject(id: String,
                  lang: Language,
                  deps: ClasspathDep[ProjectReference]*): Project = {
    Project(id = id, base = file(id))
      .dependsOn(deps.toList: _*)
      .configs(IntegrationTest)
      .settings(lang.settings: _*)
      .settings(lang.tests: _*)
  }

  def JavaProject(id: String,
                  deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Langs.Java, deps: _*)
  }

  def ScalaProject(id: String,
                   deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Langs.Scala, deps: _*)
  }

  private object Projects {

    import com.typesafe.sbt.SbtGit

    lazy val root = Seq(
      publishArtifact := false,
      publish := {},
      publishLocal := {},
      publishTo := Some(Resolver.file("devnull", file("target/devnull"))),
      SbtGit.showCurrentGitBranch,
      Packaging.writeVersion,
      Packaging.writeDpkg,
      Packaging.dpkgVersion
    ) ++ SbtGit.versionWithGit
  }
}

