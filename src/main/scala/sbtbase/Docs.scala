package sbtbase

import sbt._
import Keys._

object Docs {

  import java.util.Properties

  object Keys {
    val siteprops = SettingKey[Properties]("siteprops")
    val writeSiteprops = TaskKey[Unit]("write-siteprops")
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

  lazy val sitepropsSetting = siteprops := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
    props.setProperty("organization", organization.value)
    props.setProperty("name", name.value)

    props
  }

  lazy val writeSitepropsTask = writeSiteprops := {
    Util.writeProps(siteprops.value, "template", "sbt generated properties", (baseDirectory.value / "src" / "pamflet"))
  }

  lazy val docs = {
    Unidoc.scalaJavaUnidocSettings ++
    site.settings ++ site.publishSite() ++
    site.addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), "latest/scaladoc") ++
    site.addMappingsToSiteDir(mappings in(JavaUnidoc, packageDoc), "latest/javadoc") ++
    Seq(
      sitepropsSetting, 
      writeSitepropsTask,

      crossPaths := false,
      publishArtifact in Compile := false,
      doc in Compile <<= (doc in Compile) 
                            dependsOn (unidoc in Compile) 
                            dependsOn (makeSite in Compile)
                            dependsOn (writeSitepropsTask)
    )
  }

  lazy val pamflet = docs ++ Seq(site.pamfletSupport())
}

