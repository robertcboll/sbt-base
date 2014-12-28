package sbtbase

import sbt._
import Keys._

object Docs {

  import java.util.Properties

  object Keys {
    val siteProps = SettingKey[Properties]("siteprops")
    val writeSiteProps = TaskKey[Unit]("write-siteprops")
  }

  import Keys._

  import com.typesafe.sbt.{SbtGit => Git}
  import Git.GitKeys._

  import com.typesafe.sbt.{SbtSite => Site}
  import Site.SiteKeys._
  import Site.site

  import sbtunidoc.{Plugin => Unidoc}
  import Unidoc._
  import UnidocKeys._

  lazy val settings = {
    Unidoc.scalaJavaUnidocSettings ++
    site.settings ++ site.publishSite() ++
    site.addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), "latest/scaladoc") ++
    site.addMappingsToSiteDir(mappings in(JavaUnidoc, packageDoc), "latest/javadoc") ++
    Seq(
      sitePropsSetting, 
      writeSitePropsTask,

      crossPaths := false,
      publishArtifact in Compile := false,
      doc in Compile <<= (doc in Compile) dependsOn (unidoc in Compile) 
                                          dependsOn (makeSite in Compile),
      makeSite <<= makeSite dependsOn writeSiteProps,
      publish in Compile <<= (publish in compile) dependsOn (writeSiteProps in Compile)
    )
  }

  lazy val pamflet = settings ++ Seq(site.pamfletSupport())

  lazy val sitePropsSetting = siteProps := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))

    props
  }

  lazy val writeSitePropsTask = writeSiteProps := {
    Util.writeProps(siteProps.value, "template", "sbt generated properties", (baseDirectory.value / "src" / "pamflet"))
  }
}

