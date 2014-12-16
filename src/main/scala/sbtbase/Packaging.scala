package sbtbase

import sbt._
import Keys._

object Packaging {

  /* version writer */
  import com.typesafe.sbt.SbtGit._
  import GitKeys._

  import com.typesafe.sbt.SbtNativePackager._
  import NativePackagerKeys._

  import java.util.Properties
  import java.io._

  val writeVersionTask = TaskKey[Unit]("write-version")
  val writeDpkgTask = TaskKey[Unit]("write-dpkg")

  val writeVersion = writeVersionTask := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("release", git.baseVersion.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
    
    writeProps(props, "_version", "sbt generated version properties")
  }

  val writeDpkg = writeDpkgTask := {
    val props = new Properties()
    props.setProperty("dpkg", (version in Linux).value)

    writeProps(props, "_dpkg", "sbt generated dpkg properties")
  }

  val dpkgVersion = (version in Linux) := versionPrefix() + version.value

  def writeProps(props: Properties, filename: String, comment: String) = {
    new File("target").mkdirs()
    val fullfilename = s"target/$filename.properties"
    val out = new FileOutputStream(new File(fullfilename))
    props.store(out, s" $comment") 
  }

  def versionPrefix(): String = {
    val option = sys.props.get("build_number")
    val num = option.map(_.trim() + "-")
    num getOrElse ""
  }
}

