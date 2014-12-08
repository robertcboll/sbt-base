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

}

