package org.schemata.parser;

import com.google.protobuf.Descriptors;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;
import org.schemata.domain.Schema;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SchemaParserTest {

  private static Schema userSchema;

  @BeforeAll
  static void setUp()
      throws IOException, Descriptors.DescriptorValidationException {

    var stream = new FileInputStream(ResourceLoader.getDescriptorsPath());
    var protoFileDescriptorLoader = new ProtoFileDescriptorSetLoader(stream);
    var parser = new SchemaParser();
    var schemaList = parser.parse(protoFileDescriptorLoader.loadDescriptors());
    assertAll("User Schema Sanity Check", () -> assertNotNull(schemaList), () -> assertEquals(14, schemaList.size()));
    userSchema = schemaList.stream().filter(s -> s.name().equalsIgnoreCase("org.schemata.schema.User")).toList().get(0);
    assertNotNull(userSchema);
  }

  @Test
  @DisplayName("Test User Schema metadata")
  public void checkSchema() {
    assertAll("User Schema properties", () -> assertNotNull(userSchema),
        () -> assertEquals("org.schemata.schema.User", userSchema.name()));
  }

  @Test
  @DisplayName("Test User Fields metadata")
  public void checkFields() {
    assertAll("User Schema Fields Sanity Check", () -> assertNotNull(userSchema.fieldList()),
        () -> assertTrue(userSchema.fieldList().size() > 1));
    var fieldList = userSchema.fieldList();
    assertEquals(5, fieldList.size());
  }
}
