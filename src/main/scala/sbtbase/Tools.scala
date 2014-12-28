package sbtbase

import sbt._
import Keys._

object Tools {

  /* checkstyle */
  import com.etsy.sbt.Checkstyle._
  import CheckstyleTasks._

  lazy val style = checkstyleSettings ++
    Seq(checkstyleConfig := file("project/codequality/checkstyle.xml"))


  /* findbugs */
  import de.johoop.findbugs4sbt.FindBugs

  lazy val findbugs = FindBugs.findbugsSettings


  /* runnable */
  import spray.revolver.RevolverPlugin._

  lazy val runnable = Revolver.settings ++ Seq(
    fork := true
  )
}

