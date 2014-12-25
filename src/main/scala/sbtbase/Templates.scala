package sbtbase

import sbt._
import Keys._

object Templates {

  import Tests.Keys.AcceptanceTest
  import Langs.{Java, Scala}

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
                  lang: Language,
                  deps: ClasspathDep[ProjectReference]*): Project = {
    Project(id = id, base = file(id))
      .dependsOn(deps.toList: _*)
      .configs(IntegrationTest, AcceptanceTest)
      .settings(lang.settings: _*)
      .settings(lang.tests: _*)
  }

  def JavaProject(id: String,
                  deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Java, deps: _*)
  }

  def ScalaProject(id: String,
                   deps: ClasspathDep[ProjectReference]*): Project = {
    LangProject(id, Scala, deps: _*)
  }

  private object Projects {

    import com.typesafe.sbt.{SbtGit => Git}

    lazy val root = Seq(
      publishArtifact := false,
      publish := {},
      publishLocal := {},
      publishTo := Some(Resolver.file("devnull", file("target/devnull"))),
      Git.showCurrentGitBranch
    ) ++ Git.versionWithGit ++ Packaging.all
  }
}

