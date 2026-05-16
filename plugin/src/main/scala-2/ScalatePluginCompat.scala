package org.fusesource.scalate

import sbt.Def.Classpath
import sbt.*

private[scalate] trait ScalatePluginCompat { self: ScalatePlugin.type =>
  def managedJarsCompat(
    config: Configuration,
    jarTypes: Set[String],
    up: UpdateReport,
  ): Classpath =
    Classpaths.managedJars(config, jarTypes, up)

  def classpathToFiles(cp: Classpath): Seq[File] =
    Attributed.data(cp)
}
