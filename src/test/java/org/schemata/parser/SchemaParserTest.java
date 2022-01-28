package org.schemata.parser;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.schemata.domain.Schema;
import org.schemata.schema.UserBuilder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SchemaParserTest {

  private static Schema userSchema;

  private static DescriptorProtos.FileDescriptorSet descriptorSet;

  @BeforeAll
  static void setUp() throws IOException {
    List<GeneratedMessageV3> messages = List.of(UserBuilder.User.newBuilder().build());
    var schemaList = new SchemaParser().parseSchema(messages);
    assertAll("User Schema Sanity Check", () -> assertNotNull(schemaList), () -> assertEquals(1, schemaList.size()));
    userSchema = schemaList.get(0);

    var stream = SchemaParserTest.class.getClassLoader().getResourceAsStream("descriptors/entities.desc");
    descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(stream);
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
  public void checkDescriptorParser() {
    var parser = new SchemaParser();
    var schema = parser.parseSchema(descriptorSet);
  }
}
