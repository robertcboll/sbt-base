import sbt._
import Keys._

object Publish {

  import sbtbase._
  import com.typesafe.sbt.SbtGit.GitKeys.gitHeadCommit
 
  val repoBase = "https://build.ondeck.local/artifactory/"
  val mavenResolverBase = "repo"
  val ivyResolverBase = "ondeck-ivy"
  val gitverBase = "ondeck-ivy-gitver"
  val releaseBase = "ondeck-ivy-release"
  val mavenRelease = false

  lazy val deploy = Seq(
    updateOptions := updateOptions.value.withCachedResolution(true),

    resolvers += Resolver.url("ondeck-ivy", url(s"$repoBase$ivyResolverBase"))(Resolver.ivyStylePatterns),
    resolvers += "ondeck-maven" at s"$repoBase$mavenResolverBase",

    publishMavenStyle := mavenRelease,
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    publishTo <<= (version, gitHeadCommit) { (version, gitHeadCommit) =>
      val resolverType = if (mavenRelease) Resolver.mavenStylePatterns else Resolver.ivyStylePatterns
      val gitver = !gitHeadCommit.isEmpty && version.endsWith(gitHeadCommit.get)
      val publishRepoBase = if (gitver) gitverBase else releaseBase

      Some(Resolver.url("publish", url(s"$repoBase$publishRepoBase"))(resolverType))
    }
  )
}

