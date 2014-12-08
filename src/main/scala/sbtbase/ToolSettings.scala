package sbtbase

import sbt._
import Keys._


object ToolSettings {

  /* checkstyle */
  import com.etsy.sbt.Checkstyle._
  import CheckstyleTasks._

  lazy val checkstyle = checkstyleSettings ++
    Seq(checkstyleConfig := file("project/.checkstyle.xml"))


  /* findbugs */
  import de.johoop.findbugs4sbt.FindBugs

  lazy val findbugs = FindBugs.findbugsSettings


  /* artifactory */
  import com.typesafe.sbt.SbtGit._
  import GitKeys._
  
  val repo = ""
  val repoName = ""
  val creds = Credentials(Path.userHome / ".sbt" / ".credentials")

  lazy val artifactory = Seq(
    resolvers += "internal" at repo + "repo"
    )

  lazy val mavenPublish = Seq(
    publishTo <<= (version, gitHeadCommit) { (version, gitHeadCommit) =>
      val snapshotName = s"$repoName-gitver"
      val releaseName = s"$repoName-releases"

      val snapshots = Some(Resolver.url(snapshotName, url(repo + snapshotName))(Resolver.mavenStylePatterns))
      val releases = Some(Resolver.url(releaseName, url(repo + releaseName))(Resolver.mavenStylePatterns))

      gitHeadCommit match {
        case Some(commit) => 
          if (version.endsWith(commit)) snapshots
          else releases
        case None => releases
      }
    },
    publishMavenStyle := true,
    credentials += creds
    )

  lazy val ivyPublish = Seq(
    publishTo <<= (version, gitHeadCommit) { (version, gitHeadCommit) =>
      val snapshotName = s"$repoName-ivy-gitver"
      val releaseName = s"$repoName-ivy-release"

      val snapshots = Some(Resolver.url(snapshotName, url(artifactory + snapshotName))(Resolver.ivyStylePatterns))
      val releases = Some(Resolver.url(releaseName, url(artifactory + releaseName))(Resolver.ivyStylePatterns))

      gitHeadCommit match {
        case Some(commit) => 
          if (version.endsWith(commit)) snapshots
          else releases
        case None => releases
      }
    },
    publishMavenStyle := false,
    credentials += creds
    )
}

