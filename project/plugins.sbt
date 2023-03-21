scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.scalariform"  % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.github.sbt"     % "sbt-pgp"         % "2.2.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
