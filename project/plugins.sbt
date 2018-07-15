scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.scalariform"  % "sbt-scalariform" % "1.8.2")
addSbtPlugin("com.jsuereth"     % "sbt-pgp"         % "1.1.1")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"    % "2.3")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
