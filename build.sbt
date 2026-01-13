lazy val precompiler = (project in file("precompiler")).settings(baseSettings).settings(
  sbtPlugin := false,
  name := "scalate-precompiler",
  libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.10.1" % "compile",
  crossScalaVersions := Seq("3.3.7", "3.8.0", "2.12.21")
).disablePlugins(ScriptedPlugin)

lazy val plugin = (project in file("plugin")).settings(baseSettings).settings(
  name := "sbt-scalate-precompiler",
  sbtPlugin := true,
  crossSbtVersions := Seq("1.5.0"),
  Compile / sourceGenerators += Def.task {
    val file = (Compile / sourceManaged).value / organization.value.replace(".","/") / "Version.scala"
    val code = {
s"""package org.fusesource.scalate
object Version {
  val name    = "${name.value}"
  val version = "${version.value}"
}
""".stripMargin
    }
    IO.write(file, code)
    Seq(file)
  }.taskValue
).enablePlugins(ScriptedPlugin)

lazy val baseSettings = Seq(
  organization := "org.scalatra.scalate",
  version := "1.10.0.0-SNAPSHOT",
  Global / transitiveClassifiers := Seq(Artifact.SourceClassifier),
  Test / parallelExecution := false,
  Test / logBuffered := false,
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
  publishMavenStyle := true,
  pomIncludeRepository := { x => false },
  publishTo := (
    if (isSnapshot.value) None else Some(Opts.resolver.sonatypeStaging)
  ),
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  pomExtra := <url>https://github.com/scalate/sbt-scalate-precompiler</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses> <scm>
    <url>git@github.com:scalate/sbt-scalate-precompiler.git</url>
    <connection>scm:git:git@github.com:scalate/sbt-scalate-precompiler.git</connection>
  </scm>
  <developers>
    <developer>
      <id>casualjim</id>
      <name>Ivan Porto Carrero</name>
      <url>http://flanders.co.nz/</url>
    </developer>
    <developer>
      <id>sdb</id>
      <name>Stefan De Boey</name>
      <url>http://stefandeboey.be/</url>
    </developer>
    <developer>
      <id>BowlingX</id>
      <name>David Heidrich</name>
      <url>http://www.myself-design.com/</url>
    </developer>
    <developer>
      <id>seratch</id>
      <name>Kazuhiro Sera</name>
      <url>http://git.io/sera</url>
    </developer>
  </developers>
)
