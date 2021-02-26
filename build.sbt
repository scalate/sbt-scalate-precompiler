lazy val precompiler = (project in file("precompiler")).settings(baseSettings).settings(
  sbtPlugin := false,
  name := "scalate-precompiler",
  libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.6" % "compile",
  crossScalaVersions := Seq("2.13.2", "2.12.13", "2.11.12")
).disablePlugins(ScriptedPlugin)

lazy val plugin = (project in file("plugin")).settings(baseSettings).settings(
  name := "sbt-scalate-precompiler",
  sbtPlugin := true,
  crossSbtVersions := Seq("1.3.10"),
  sourceGenerators in Compile += Def.task {
    val file = (sourceManaged in Compile).value / organization.value.replace(".","/") / "Version.scala"
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
  version := "1.9.6.0",
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  parallelExecution in Test := false,
  logBuffered in Test := false,
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
  publishMavenStyle := true,
  pomIncludeRepository := { x => false },
  resolvers += "sonatype staging" at "https://oss.sonatype.org/content/repositories/staging",
  publishTo := Some(
    if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging
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
