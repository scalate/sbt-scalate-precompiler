import org.fusesource.scalate.ScalatePlugin._
import ScalateKeys._

scalaVersion := "2.12.21"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.10.1" % "compile"

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
TaskKey[Unit]("check") := {
  val outputDir = (Compile / sourceManaged).value
  val scalaFile = outputDir / "scalate" / "templates" / "index_ssp.scala"
  if (!scalaFile.exists) {
    sys.error(s"${scalaFile.getAbsolutePath} doesn't exist.")
  }
  ()
}
