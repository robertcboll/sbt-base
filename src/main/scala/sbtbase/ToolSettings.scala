package sbtbase

import sbt._
import Keys._


object ToolSettings {

  /* checkstyle */
  import com.etsy.sbt.Checkstyle._
  import CheckstyleTasks._

  lazy val style = checkstyleSettings ++
    Seq(checkstyleConfig := file("project/codequality/checkstyle.xml"))


  /* findbugs */
  import de.johoop.findbugs4sbt.FindBugs

  lazy val findbugs = FindBugs.findbugsSettings


  /* version writer */
  import com.typesafe.sbt.SbtGit._
  import GitKeys._

  import com.typesafe.sbt.SbtNativePackager._
  import NativePackagerKeys._

  import java.util.Properties
  import java.io._

  def writeProps(props: Properties, filename: String, comment: String) = {
    new File("target").mkdirs()
    val fullfilename = s"target/$filename.properties"
    val out = new FileOutputStream(new File(fullfilename))
    props.store(out, s" $comment") 
  }

  val writeVersionTask = TaskKey[Unit]("write-version")
  val writeVersion = writeVersionTask := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("release", git.baseVersion.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
    
    writeProps(props, "_version", "sbt generated version properties")
  }

  val writeDpkgTask = TaskKey[Unit]("write-dpkg")
  val writeDpkg = writeDpkgTask := {
    val props = new Properties()
    props.setProperty("dpkg", (version in Linux).value)

    writeProps(props, "_dpkg", "sbt generated dpkg properties")
  }

  def versionPrefix(): String = {
    val option = sys.props.get("build_number")
    val num = option.map(_.trim() + "-")
    num getOrElse ""
  }

  val dpkgVersion = (version in Linux) := versionPrefix() + version.value
}

