import org.fusesource.scalate.ScalatePlugin._
import ScalateKeys._

scalaVersion := "2.12.20"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.10.1" % "compile"

scalateSettings ++ Seq(
  scalateOverwrite := true
)
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

TaskKey[Unit]("recordModifiedTime") := {
  val base = (Compile / sourceManaged).value
  val recorded = base / "index_ssp.scala"
  IO.touch(recorded, true)
}

TaskKey[Unit]("checkCompiled") := {
  val base = (Compile / sourceManaged).value
  val generated = base / "scalate" / "templates" / "index_ssp.scala"
  if (!generated.exists) {
    sys.error(s"${generated.getAbsolutePath} doesn't exist.")
  }
  ()
}

TaskKey[Unit]("checkRecompiled") := {
  val base = (Compile / sourceManaged).value
  val recorded = base / "index_ssp.scala"
  val generated = base / "scalate" / "templates" / "index_ssp.scala"
  if (!generated.exists) {
    sys.error(s"${generated.getAbsolutePath} doesn't exist.")
  }
  if (recorded.lastModified > generated.lastModified) {
    sys.error(s"${generated.getAbsolutePath} are not recompiled.")
  }
  ()
}
