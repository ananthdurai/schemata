package org.schemata;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ResourceLoader {

  public static String getDescriptorsPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    return absolutePath + "/descriptors/model.desc";
  }

  public static String getChangedDescriptorsPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    return absolutePath + "/descriptors/changed_model.desc";
  }

  public static String getProtoEntitiesPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    return absolutePath + "/schema/entities.proto";
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
    Path resourceDirectory = Paths.get("src", "test", "resources");
    return resourceDirectory.toFile().getAbsolutePath() + "/dbt";
  }

  public static String getInvalidDbtBasePath() {
    Path resourceDirectory = Paths.get("src", "main", "dbt");
    return resourceDirectory.toFile().getAbsolutePath();
  }
}
