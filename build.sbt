import ReleaseTransformations._

def sbt2 = "2.0.0-RC14"

def Scala3 = "3.3.7"
def Scala213 = "2.13.18"
def Scala212 = "2.12.21"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommandAndRemaining("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges,
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

lazy val plugin = projectMatrix
  .in(file("plugin"))
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions =
    Seq(
      Scala212,
      scala_version_from_sbt_version.ScalaVersionFromSbtVersion(sbt2),
    )
  )
  .settings(baseSettings)
  .settings(
    name := "sbt-scalate-precompiler",
    sbtPlugin := true,
    addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.1.0"),
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
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          "1.9.9"
        case _ =>
          sbt2
      }
    },
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

lazy val baseSettings = Seq(
  organization := "io.github.scalate",
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
