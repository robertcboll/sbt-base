organization := "com.robertcboll"

name := "sbt-plugins"

version := "0.1-SNAPSHOT"

sbtPlugin := true

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// version
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

// docs
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

// packaging
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.5-RC1")

// coverage
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")

// running
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

