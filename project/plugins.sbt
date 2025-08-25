scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
