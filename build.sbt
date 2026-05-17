def Scala3 = "3.3.7"
def Scala213 = "2.13.18"
def Scala212 = "2.12.21"

lazy val interface = project
  .in(file("interface"))
  .settings(baseSettings)
  .settings(
    autoScalaLibrary := false,
    crossPaths := false,
    javacOptions ++= Seq(
      "-source",
      "1.8",
      "-target",
      "1.8",
    ),
    Compile / doc / javacOptions := Nil,
    name := "scalate-precompiler-interface",
  )

lazy val precompiler = projectMatrix
  .in(file("precompiler"))
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = Seq(Scala212, Scala213, Scala3))
  .settings(baseSettings)
  .settings(
    sbtPlugin := false,
    scalacOptions += "-release:8",
    name := "scalate-precompiler",
    libraryDependencies += "io.github.scalate" %% "scalate-core" % "1.11.0",
  )
  .disablePlugins(ScriptedPlugin)
  .dependsOn(interface)

lazy val plugin = projectMatrix
  .in(file("plugin"))
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = Seq(Scala212))
  .settings(baseSettings)
  .settings(
    name := "sbt-scalate-precompiler",
    sbtPlugin := true,
    scalacOptions ++= {
      scalaBinaryVersion.value match {
        case "3" =>
          Nil
        case _ =>
          Seq(
            "-release:8",
          )
      }
    },
    crossSbtVersions := Seq("1.9.9"),
    scriptedLaunchOpts ++= Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value,
    ),
    scriptedBufferLog := false,
    Compile / sourceGenerators += Def.task {
      val file = (Compile / sourceManaged).value / organization.value.replace(".", "/") / "Version.scala"
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
  )
  .enablePlugins(ScriptedPlugin)
  .dependsOn(interface)

lazy val baseSettings = Seq(
  organization := "io.github.scalate",
  version := "1.10.0.0-SNAPSHOT",
  Global / transitiveClassifiers := Seq(Artifact.SourceClassifier),
  Test / parallelExecution := false,
  Test / logBuffered := false,
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-Xsource:3",
        )
      case "2.13" =>
        Seq(
          "-Xsource:3-cross",
        )
      case "3" =>
        Nil
    }
  },
  javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
  publishMavenStyle := true,
  pomIncludeRepository := { x => false },
  publishTo := (
    if (isSnapshot.value) None else localStaging.value
  ),
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

publish / skip := true
autoScalaLibrary := false
