import org.fusesource.scalate.ScalatePlugin._
import ScalateKeys._

scalaVersion := "2.12.8"

resolvers += Resolver.file("ivy-local", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.mavenStylePatterns)

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.8" % "compile"

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
