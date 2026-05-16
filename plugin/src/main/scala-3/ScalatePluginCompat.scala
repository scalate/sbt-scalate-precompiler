package org.fusesource.scalate

import sbt.Def.Classpath
import sbt.*
import sbt.Keys.*

private[scalate] trait ScalatePluginCompat { self: ScalatePlugin.type =>
  inline def managedJarsCompat(
    config: Configuration,
    jarTypes: Set[String],
    up: UpdateReport,
  ): Classpath =
    Classpaths.managedJars(config, jarTypes, up, fileConverter.value)

  def classpathToFiles(cp: Classpath)(using converter: FileConverter): Seq[File] =
    cp.map(x => converter.toPath(x.data).toFile)
}
