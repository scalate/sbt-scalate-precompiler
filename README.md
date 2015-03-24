# sbt-scalate-precompiler
 
Integration for SBT that lets you generate sources for your Scalate templates and precompile them as part of the normal compilation process. 
This plugin is published to sonatype oss repository.
 
## Usage

### Getting the plugin

Include the plugin in `project/plugins.sbt`:

```scala
addSbtPlugin("org.skinny-framework" % "sbt-scalate-precompiler" % "1.7.1.0-SNAPSHOT")
```

Configure the plugin in `build.sbt`:

```scala

import ScalateKeys._

seq(scalateSettings:_*)
      
// Scalate Precompilation and Bindings
scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
  Seq(
    TemplateConfig(
      base / "webapp" / "WEB-INF" / "webTmpl",
      Seq(
        "import org.myapp.scalate.Helpers._",
        "import org.myapp.model._",
        "import net.liftweb.common._",
        "import org.joda.time._",
        "import org.scalatra.UrlGenerator"
      ),
      Seq(
        Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true),
        Binding("messageTranslatorModel", "org.myapp.model.mongo.MessageTranslator", importMembers = true, isImplicit = true, defaultValue = null),
        Binding("userSession", "org.myapp.auth.UserSession", importMembers = true, defaultValue = null),
        Binding("env", "org.myapp.util.Environment")
      ),
      Some("webTmpl")
    ),
    TemplateConfig(
      base / "webapp" / "WEB-INF" / "mailTmpl",
      Seq(
        "import org.myapp.scalate.Helpers._",
        "import org.myapp.model._",
        "import net.liftweb.common._",
        "import org.joda.time._"
      ),
      Seq(
        Binding("i18n", "org.myapp.model.mongo.MessageTranslator", true, isImplicit = true, defaultValue = null),
        Binding("user", "User", false, defaultValue = null),
        Binding("config", "com.typesafe.config.Config", false, defaultValue = null),
        Binding("assets", "org.myapp.model.mongo.fields.AssetPaths", false, isImplicit = true, defaultValue = null),
        Binding("geonames", "org.myapp.model.Geonames", false, isImplicit = true, defaultValue = null)
      ),
      Some("mailTmpl")
    )
  )
}

```

### Configuring the plugin in `project/build.scala`

Configure the plugin in `project/build.scala`:

```scala

import sbt._
import sbt.Keys._
import skinny.scalate.ScalatePlugin._
import ScalateKeys._

object build extends Build {  
  val templateSettings = scalateSettings ++ Seq(
    /**
     * Sets the behavior of recompiling template files.
     * Always template files are recompiled when this setting is true.
     * When you set it to false, they are recompiled only when the modified time of
     * a template file is newer than that of a scala file generated by compilation
     * or a compiled scala file corresponding to a template file doesn't exist yet.
     */
    scalateOverwrite := true,
    scalateTemplateConfig in Compile <<= (baseDirectory) { base =>
      Seq(
        /**
         * A minimal template configuration example.
         * "scalate" is used as a package prefix(the 4th argument of TemplateConfig.apply)
         * if not specified.
         *
         * An example of a scalate usage is as bellow if you have templates/index.ssp.
         *
         * val engine = new TemplateEngine
         * engine.layout("/scalate/index.ssp")
         */
        TemplateConfig(
          base / "templates",
          Nil,
          Nil
        )
      )
    }
  )

  lazy val root = Project("root", file(".")).settings(templateSettings:_*)
}

```

### Trigger recompilation on save

From version 0.2.2 onwards the plugin detects when sources are changed and will trigger a recompilation.
Older versions can add this to their build.sbt:

```scala
watchSources <++= (scalateTemplateDirectory in Compile) map (d => (d ** "*").get)
```

### To use multiiple template directories with scalatra you'll need to make some changes too: 

```scala
trait YourScalateSupport extends ScalateSupport {
 
  override protected def defaultTemplatePath: List[String] = List("/webTmpl/views")
 
  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
 
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      TemplateEngine.templateTypes.map("/webTmpl/layouts/default." + _): _*)
 
    engine.packagePrefix = "webTmpl"
    engine
  }
 
}
```


## Patches

Patches are gladly accepted from their original author. Along with any patches, please state that the patch is your original work and that you license the work to the *xsbt-scalate-generate* project under the MIT License.
 
## License
 
the MIT license

