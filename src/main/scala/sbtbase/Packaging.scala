package sbtbase

import sbt._
import Keys._

object Packaging {

  object Keys {
    val writeVersionTask = TaskKey[Unit]("write-version")
    val writeDpkgTask = TaskKey[Unit]("write-dpkg")
  }

  import Keys._

  import com.typesafe.sbt.{SbtGit => Git}
  import Git.GitKeys._
  import Git.git

  import com.typesafe.sbt.{SbtNativePackager => Packager}
  import Packager.NativePackagerKeys._
  import Packager.Linux

  import java.util.Properties
  import java.io.{File, FileOutputStream}

  val all = Seq(writeVersion, writeDpkg, dpkgVersion)

  lazy val writeVersion = writeVersionTask := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("release", git.baseVersion.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
    
    Util.writeProps(props, "_version", "sbt generated version properties", target.value)
  }

  lazy val writeDpkg = writeDpkgTask := {
    val props = new Properties()
    props.setProperty("dpkg", (version in Linux).value)

    Util.writeProps(props, "_dpkg", "sbt generated dpkg properties", target.value)
  }

  lazy val dpkgVersion = (version in Linux) := versionPrefix() + version.value

  private[this] def versionPrefix(): String = {
    val option = sys.props.get("build_number")
    val num = option.map(_.trim() + "-")
    num getOrElse ""
  }
}

