name := "heroku-openid"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  ws,
  "org.jscience" % "jscience" % "4.3.1",
  "org.bitbucket.b_c" % "jose4j" % "0.5.0",
  "com.nimbusds" % "nimbus-jose-jwt" % "2.22.1"
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )
