package io.steeltoe.sbt

import sbt._
import sbt.Keys._

class ExtraSettings {

  /* runnable */
  import spray.revolver.RevolverPlugin._

  lazy val runnable = Revolver.settings ++
    Seq(fork := true)


  /* checkstyle */
  import com.etsy.sbt.Checkstyle._
  import CheckstyleTasks._

  lazy val checkstyle = checkstyleSettings ++
    Seq(checkstyleConfig := file("codequality/checstyle.xml"))
}
