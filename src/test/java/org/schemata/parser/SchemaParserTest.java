package org.schemata.parser;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.schemata.domain.Schema;
import org.schemata.schema.SchemataBuilder;
import org.schemata.schema.UserBuilder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SchemaParserTest {

  private static Schema userSchema;

  @BeforeAll
  static void setUp() {
    List<GeneratedMessageV3> messages = List.of(UserBuilder.User.newBuilder().build());
    var precompiledLoader = new PrecompiledLoader(messages);
    var parser = new SchemaParser();
    var schemaList = parser.parse(precompiledLoader.loadDescriptors());
    assertAll("User Schema Sanity Check", () -> assertNotNull(schemaList), () -> assertEquals(1, schemaList.size()));
    userSchema = schemaList.get(0);
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

  @Test
  @DisplayName("Test loading FileDescriptorSet from disk")
  public void checkDescriptorParser() throws Descriptors.DescriptorValidationException, IOException {
    var stream = loadResourceStream("descriptors/entities.desc");
    var protoFileLoader = new ProtoFileDescriptorSetLoader(stream);
    var parser = new SchemaParser();
    var descriptors = protoFileLoader.loadDescriptors();
    var schemas = parser.parse(descriptors);

    assertEquals(2, schemas.size(), "expected schema for both entities");
    var departmentSchema = schemas.get(0);
    assertEquals("org.entities.Department", departmentSchema.name());

    var personSchema = schemas.get(1);
    assertEquals("org.entities.Person", personSchema.name());
  }

  private InputStream loadResourceStream(String path) {
    return SchemaParserTest.class.getClassLoader().getResourceAsStream(path);
  }
}
