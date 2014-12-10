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


  /* version writer */
  import com.typesafe.sbt.SbtGit._
  import GitKeys._

  import java.util.Properties
  import java.io._

  val writeVersionTask = TaskKey[Unit]("write-version")
  val writeVersion = writeVersionTask := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("release", git.baseVersion.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
      
    val filename = "target/_version.properties"
    val out = new FileOutputStream(new File(filename))
    props.store(out, " sbt generated version properties") 
  }
}

