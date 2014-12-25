package sbtbase

import sbt._
import Keys._

object Docs {

  import com.typesafe.sbt.{SbtSite => Site}
  import Site.SiteKeys._
  import Site.site

  import sbtunidoc.{Plugin => Unidoc}
  import Unidoc._
  import UnidocKeys._

  lazy val docs = {
    Unidoc.scalaJavaUnidocSettings ++
    site.settings ++ site.publishSite() ++
    site.addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), "latest/scaladoc") ++
    site.addMappingsToSiteDir(mappings in(JavaUnidoc, packageDoc), "latest/javadoc") ++
    Seq(
      crossPaths := false,
      publishArtifact in Compile := false,
      doc in Compile <<= (doc in Compile) 
                            dependsOn (unidoc in Compile) 
                            dependsOn (makeSite in Compile)
    )
  }

  lazy val pamflet = docs ++ Seq(site.pamfletSupport())
}

