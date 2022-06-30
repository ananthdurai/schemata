package org.schemata;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ResourceLoader {

  public static String getDescriptorsPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    return absolutePath + "/descriptors/entities.desc";
  }

  public static String getAvroSchemaPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    return resourceDirectory.toFile().getAbsolutePath();
  }

  public static String getBrandSchemaPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    return resourceDirectory.toFile().getAbsolutePath() + "/avro_schema/brand.avsc";
  }

  public static String getInValidBrandSchemaPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    return resourceDirectory.toFile().getAbsolutePath() + "/avro_schema/brand_dummy.avsc";
  }

  public static String getDbtBasePath() {
    Path resourceDirectory = Paths.get("src", "main", "resources", "dbt");
    return resourceDirectory.toFile().getAbsolutePath();
  }

  public static String getInvalidDbtBasePath() {
    Path resourceDirectory = Paths.get("src", "main", "dbt");
    return resourceDirectory.toFile().getAbsolutePath();
  }
}
