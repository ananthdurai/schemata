package org.schemata;

import java.nio.file.Path;
import java.nio.file.Paths;


public class TestResourceLoader {

  public static String getDescriptorsPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    System.out.println(absolutePath);
    return absolutePath + "/descriptors/entities.desc";
  }
}
