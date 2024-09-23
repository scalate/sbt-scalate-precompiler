{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null) {
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  } else {
    addSbtPlugin("org.scalatra.scalate" % "sbt-scalate-precompiler" % pluginVersion)
  }
}
