package org.fusesource.scalate;

import java.io.File;

public interface PrecompilerInterface {
  void setSources(File source);
  void setTargetDirectory(File directory);
  void setLogConfig(File config);
  void setOverwrite(boolean overwrite);
  void setScalateImports(String[] imports);
  void setScalateBindings(Object[][] bindings);
  void setPackagePrefix(String prefix);
  File[] execute();
}
