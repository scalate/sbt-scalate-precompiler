scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

libraryDependencies += "com.github.xuwei-k" %% "scala-version-from-sbt-version" % "0.1.0"
