import org.fusesource.scalate.ScalatePlugin._
import ScalateKeys._

scalaVersion := "2.12.21"

crossScalaVersions ++= Seq("2.13.18", "3.3.8")

libraryDependencies += "io.github.scalate" %% "scalate-core" % "1.11.0" % "compile"

scalateSettings
Compile / scalateTemplateConfig := {
  val base = (Compile / sourceDirectory).value
  Seq(
    TemplateConfig(
      base / "templates",
      Nil,
      Nil
    )
  )
}
InputKey[Unit]("check") := {
  val outputDir = (Compile / sourceManaged).value
  val scalaFile = outputDir / "scalate" / "templates" / "index_ssp.scala"
  if (!scalaFile.exists) {
    sys.error(s"${scalaFile.getAbsolutePath} doesn't exist.")
  }
  ()
}
