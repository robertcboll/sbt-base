package sbtbase

import sbt._
import Keys._

object Packaging {

  object Keys {
    import java.util.Properties

    val versionProps = SettingKey[Properties]("versionprops")
    val writeVersionProps = TaskKey[Unit]("write-versionprops")
  }

  import Keys._

  import com.typesafe.sbt.{SbtGit => Git}
  import Git.GitKeys._
  import Git.git

  import com.typesafe.sbt.{SbtNativePackager => Packager}
  import Packager.NativePackagerKeys._
  import Packager.Linux
  import Packager.Debian

  import java.util.Properties
  import java.io.{File, FileOutputStream}

  val settings: Seq[sbt.Def.Setting[_]] = Seq(
    versionPropsSetting,
    writeVersionPropsTask, 
    linuxVersionSetting,
    publish <<= publish dependsOn writeVersionProps
    )
  
  lazy val versionPropsSetting = versionProps := {
    val props = new Properties()
    props.setProperty("version", version.value)
    props.setProperty("release", git.baseVersion.value)
    props.setProperty("gitsha", gitHeadCommit.value.getOrElse(""))
    props.setProperty("dpkg", (version in Linux).value)

    props
  }

  lazy val writeVersionPropsTask = writeVersionProps := {    
    Util.writeProps(versionProps.value, "_version", "sbt generated version properties", target.value)
  }

  lazy val linuxVersionSetting = (version in Linux) := versionPrefix() + version.value

  private[this] def versionPrefix(): String = {
    val option = sys.props.get("build_number")
    val num = option.map(_.trim() + "-")
    num getOrElse ""
  }
}

