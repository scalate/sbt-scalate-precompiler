import org.fusesource.scalate.ScalatePlugin._
import ScalateKeys._

scalaVersion := "2.12.8"

resolvers += Resolver.file("ivy-local", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.mavenStylePatterns)

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.8" % "compile"

scalateSettings ++ Seq(
  scalateOverwrite := false
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

TaskKey[Unit]("updateModifiedTime") := {
  val base = (Compile / sourceManaged).value
  val generated = base / "scalate" / "templates" / "index_ssp.scala"
  IO.touch(generated, true)
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

TaskKey[Unit]("checkNotRecompiled") := {
  val base = (Compile / sourceManaged).value
  val generated = base / "scalate" / "templates" / "index_ssp.scala"
  val recorded = base / "index_ssp.scala"
  if (!generated.exists) {
    sys.error(s"${generated.getAbsolutePath} doesn't exist.")
  }
  if (recorded.lastModified < generated.lastModified) {
    sys.error(s"${generated.getAbsolutePath} are not recompiled.")
  }
  ()
}
