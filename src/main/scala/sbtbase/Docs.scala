package sbtbase

import sbt._
import Keys._

object Docs {

  import com.typesafe.sbt.SbtSite.SiteKeys._
  import com.typesafe.sbt.SbtSite.site

  import sbtunidoc.Plugin.UnidocKeys._
  import sbtunidoc.{Plugin => Unidoc}

  lazy val docs = {
    Unidoc.scalaJavaUnidocSettings ++
    site.settings ++ site.publishSite() ++
    site.addMappingsToSiteDir(mappings in(Unidoc.ScalaUnidoc, packageDoc), "latest/scaladoc") ++
    site.addMappingsToSiteDir(mappings in(Unidoc.JavaUnidoc, packageDoc), "latest/javadoc") ++
    Seq(
      crossPaths := false,
      publishArtifact in Compile := false,
      doc in Compile <<= (doc in Compile).dependsOn(unidoc in Compile),
      doc in Compile <<= (doc in Compile).dependsOn(makeSite in Compile)
    )
  }

  lazy val pamflet = docs ++ Seq(site.pamfletSupport())
}

