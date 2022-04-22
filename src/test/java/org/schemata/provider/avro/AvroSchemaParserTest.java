package org.schemata.provider.avro;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;
import org.schemata.domain.EventType;
import org.schemata.domain.SchemaType;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AvroSchemaParserTest {

  @Test
  public void testListAvroSchemaFilesWithInvalidPath() {
    assertThrows(IOException.class, () -> {
      String avroSchemaPath = ResourceLoader.getAvroSchemaPath() + "dummy_path";
      var parser = new AvroSchemaParser();
      parser.listAvroSchemaFiles(avroSchemaPath);
    });
  }

  @Test
  public void testListAvroSchemaFiles()
      throws IOException {
    String avroSchemaPath = ResourceLoader.getAvroSchemaPath();
    var parser = new AvroSchemaParser();
    var schemaFileList = parser.listAvroSchemaFiles(avroSchemaPath);
    assertAll("Assert list of schema files",
        () -> assertEquals(1, schemaFileList.size()));
  }

  @Test
  public void testCompileAvroSchemaWithInvalidFile() {
    assertThrows(IOException.class, () -> {
      var parser = new AvroSchemaParser();
      parser.compileAvroSchema(ResourceLoader.getInValidBrandSchemaPath());
    });
  }

  @Test
  public void testCompileAvroSchema()
      throws IOException {
    var parser = new AvroSchemaParser();
    var schema = parser.compileAvroSchema(ResourceLoader.getBrandSchemaPath());
    assertEquals("org.schemata.schema.Brand", schema.getFullName());
  }

  @Test
  public void testBuildSchema()
      throws IOException {
    var parser = new AvroSchemaParser();
    var schema = parser.buildSchema(ResourceLoader.getBrandSchemaPath());
    assertAll("Assert schema properties",
        () -> assertEquals("org.schemata.schema.Brand", schema.name()),
        () -> assertEquals("This is the description of the Brand table", schema.description()),
        () -> assertEquals(SchemaType.ENTITY.name(), schema.type()),
        () -> assertEquals(EventType.NONE.name(), schema.eventType()),
        () -> assertEquals(3, schema.fieldList().size())
    );
    System.out.println(schema);
  }
}
