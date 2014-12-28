package sbtbase

import sbt._
import Keys._

object Util {

  import java.util.Properties
  import java.io.{File, FileOutputStream}

  def writeProps(props: Properties, filename: String, comment: String, target: File) = {
    target.mkdirs()
    val file = new File(target, s"$filename.properties")
    val out = new FileOutputStream(file)
    props.store(out, s" $comment") 
  }
}

