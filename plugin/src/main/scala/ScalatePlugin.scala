package org.fusesource.scalate

import scala.language.reflectiveCalls
import sbt.*
import sbt.internal.inc.classpath.ClasspathUtilities
import Keys.*
import sbt.Def.Initialize
import xsbti.FileConverter
import java.io.File
import scala.jdk.CollectionConverters.*
import sbtcompat.PluginCompat.*

object ScalatePlugin extends AutoPlugin with ScalatePluginCompat {

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
    @transient
    val scalateClasspaths = taskKey[ScalateClasspaths]("")
  }

  import ScalateKeys.*

  def scalateSourceGeneratorTask: Initialize[Task[Seq[File]]] = Def.task {
    implicit val converter: FileConverter = fileConverter.value
    generateScalateSource(
      streams.value,
      new File((Compile / sourceManaged).value, "scalate"),
      (Compile / scalateLoggingConfig).value,
      (scalateClasspaths / managedClasspath).value,
      (Compile / scalateOverwrite).value,
      (Compile / scalateTemplateConfig).value
    )
  }

  type Generator = {
    var packagePrefix: String
    var sources: File
    var targetDirectory: File
    var logConfig: File
    var overwrite: Boolean
    var scalateImports: java.util.List[String]
    var scalateBindings: java.util.List[java.util.List[AnyRef]]
    def execute(): java.util.List[File]
  }

  final case class ScalateClasspaths(classpath: PathFinder, scalateClasspath: PathFinder)

  def scalateClasspathsTask(cp: Classpath, scalateCp: Classpath)(implicit converter: FileConverter) = {
    ScalateClasspaths(
      cp.map(_.data).map(sbtcompat.PluginCompat.toFile),
      scalateCp.map(_.data).map(sbtcompat.PluginCompat.toFile)
    )
  }

  def generateScalateSource(
    out: TaskStreams,
    outputDir: File,
    logConfig: File,
    cp: Classpath,
    overwrite: Boolean,
    templates: Seq[TemplateConfig]
  )(implicit converter: FileConverter): Seq[File] = {
    withScalateClassLoader(classpathToFiles(cp)) { classLoader =>
      templates flatMap { t =>
        val className = "org.fusesource.scalate.Precompiler"
        val klass = classLoader.loadClass(className)
        val generator = klass.getDeclaredConstructor().newInstance().asInstanceOf[Generator]

        val source = t.scalateTemplateDirectory
        out.log.info("Compiling Templates in Template Directory: %s" format t.scalateTemplateDirectory.getAbsolutePath)

        val preservedLogbackConfiguration = Option(System.getProperty("logback.configurationFile"))
        val targetDirectory = outputDir / source.getName
        // Because we have to Scope each Template Folder we need to create unique package names
        generator.packagePrefix = t.packagePrefix getOrElse source.getName
        generator.sources = source
        generator.targetDirectory = targetDirectory
        generator.logConfig = logConfig
        generator.overwrite = overwrite
        generator.scalateImports = java.util.Arrays.asList(t.scalateImports*)
        generator.scalateBindings = java.util.Arrays.asList(t.scalateBindings.map { b =>
          java.util.Arrays.asList(
            b.name.asInstanceOf[AnyRef],
            b.className.asInstanceOf[AnyRef],
            b.importMembers.asInstanceOf[AnyRef],
            b.defaultValue.asInstanceOf[AnyRef],
            b.kind.asInstanceOf[AnyRef],
            b.isImplicit.asInstanceOf[AnyRef]
          )
        }*)
        try {
          generator.execute().asScala.toList
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
    watchSources ++= Def.uncached(
      (Compile / scalateTemplateConfig).value
        .map(_.scalateTemplateDirectory)
        .flatMap(d => (d ** "*").get())
    ),
    scalateOverwrite := true,
    scalateClasspaths / managedClasspath := Def.uncached(
      managedJarsCompat(Scalate, classpathTypes.value, update.value)
    ),
    scalateClasspaths := {
      implicit val converter: FileConverter = fileConverter.value
      scalateClasspathsTask(
        (Runtime / fullClasspath).value,
        (scalateClasspaths / managedClasspath).value
      )
    }
  )

  /**
   * Runs a block of code with the Scalate classpath as the context class loader.
   * The Scalate classpath is the runClassPath plus the buildScalaInstance's jars.
   */
  protected def withScalateClassLoader[A](runClassPath: Seq[File])(f: ClassLoader => A): A = {
    val oldLoader = Thread.currentThread.getContextClassLoader
    val loader = ClasspathUtilities.toLoader(runClassPath)
    Thread.currentThread.setContextClassLoader(loader)
    try {
      f(loader)
    } finally {
      Thread.currentThread.setContextClassLoader(oldLoader)
    }
  }

}
