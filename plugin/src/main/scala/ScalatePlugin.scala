package org.fusesource.scalate

import sbt.*
import Keys.*
import Def.Initialize
import java.io.File
import java.net.URLClassLoader
import java.util.ServiceLoader
import scala.jdk.CollectionConverters.*

object ScalatePlugin extends AutoPlugin {

  case class Binding(
    name: String,
    className: String = "Any",
    importMembers: Boolean = false,
    defaultValue: String = "",
    kind: String = "val",
    isImplicit: Boolean = false
  )

  /**
   * Template Configuration
   */
  case class TemplateConfig(
    scalateTemplateDirectory: File,
    scalateImports: Seq[String],
    scalateBindings: Seq[Binding],
    packagePrefix: Option[String] = Some("scalate")
  )

  val Scalate = config("scalate").hide

  object ScalateKeys {
    val scalateTemplateConfig =
      settingKey[Seq[TemplateConfig]]("Different Template Configurations")
    val scalateLoggingConfig =
      settingKey[File]("Logback config to get rid of that infernal debug output.")
    val scalateOverwrite =
      settingKey[Boolean]("Always generate the Scala sources even when they haven't changed")
    val scalateClasspaths = taskKey[ScalateClasspaths]("")
  }

  import ScalateKeys.*

  def scalateSourceGeneratorTask: Initialize[Task[Seq[File]]] = Def.task {
    generateScalateSource(
      streams.value,
      new File((Compile / sourceManaged).value, "scalate"),
      (Compile / scalateLoggingConfig).value,
      (scalateClasspaths / managedClasspath).value,
      (Compile / scalateOverwrite).value,
      (Compile / scalateTemplateConfig).value
    )
  }

  final case class ScalateClasspaths(classpath: PathFinder, scalateClasspath: PathFinder)

  def scalateClasspathsTask(cp: Classpath, scalateCp: Classpath) =
    ScalateClasspaths(cp.map(_.data), scalateCp.map(_.data))

  def generateScalateSource(
    out: TaskStreams,
    outputDir: File,
    logConfig: File,
    cp: Classpath,
    overwrite: Boolean,
    templates: Seq[TemplateConfig]
  ) = {
    withScalateClassLoader(cp.files) { classLoader =>
      templates.flatMap { t =>
        val List(generator) = ServiceLoader
          .load(classOf[org.fusesource.scalate.PrecompilerInterface], classLoader)
          .iterator()
          .asScala
          .toList
        val source = t.scalateTemplateDirectory
        out.log.info("Compiling Templates in Template Directory: %s" format t.scalateTemplateDirectory.getAbsolutePath)

        val preservedLogbackConfiguration = Option(System.getProperty("logback.configurationFile"))
        val targetDirectory = outputDir / source.getName
        // Because we have to Scope each Template Folder we need to create unique package names
        generator.setPackagePrefix(t.packagePrefix getOrElse source.getName)
        generator.setSources(source)
        generator.setTargetDirectory(targetDirectory)
        generator.setLogConfig(logConfig)
        generator.setOverwrite(overwrite)
        generator.setScalateImports(t.scalateImports.toArray)
        generator.setScalateBindings(t.scalateBindings.toArray.map { b =>
          Array(
            b.name.asInstanceOf[AnyRef],
            b.className.asInstanceOf[AnyRef],
            b.importMembers.asInstanceOf[AnyRef],
            b.defaultValue.asInstanceOf[AnyRef],
            b.kind.asInstanceOf[AnyRef],
            b.isImplicit.asInstanceOf[AnyRef]
          )
        })
        try {
          generator.execute.toList
        } finally {
          preservedLogbackConfiguration match {
            case Some(oldConfig) => System.setProperty("logback.configurationFile", oldConfig)
            case None => System.clearProperty("logback.configurationFile")
          }
        }
      }
    }
  }

  val scalateSettings: Seq[sbt.Def.Setting[?]] = Seq(
    ivyConfigurations += Scalate,
    Compile / scalateTemplateConfig := Seq(
      TemplateConfig(file(".") / "src" / "main" / "webapp" / "WEB-INF", Nil, Nil, Some("scalate"))
    ),
    Compile / scalateLoggingConfig := (Compile / resourceDirectory).value / "logback.xml",
    libraryDependencies += "io.github.scalate" %% "scalate-precompiler" % Version.version % Scalate.name,
    Compile / sourceGenerators += scalateSourceGeneratorTask.taskValue,
    watchSources ++= (Compile / scalateTemplateConfig).value
      .map(_.scalateTemplateDirectory)
      .flatMap(d => (d ** "*").get()),
    scalateOverwrite := true,
    scalateClasspaths / managedClasspath := Classpaths.managedJars(Scalate, classpathTypes.value, update.value),
    scalateClasspaths := scalateClasspathsTask(
      (Runtime / fullClasspath).value,
      (scalateClasspaths / managedClasspath).value
    )
  )

  /**
   * Runs a block of code with the Scalate classpath as the context class loader.
   * The Scalate classpath is the runClassPath plus the buildScalaInstance's jars.
   */
  private def withScalateClassLoader[A](runClassPath: Seq[File])(f: ClassLoader => A): A = {
    val loader = new URLClassLoader(
      runClassPath.map(_.toURI.toURL).toArray,
      classOf[org.fusesource.scalate.PrecompilerInterface].getClassLoader
    )
    try {
      f(loader)
    } finally {
      loader.close()
    }
  }

}
