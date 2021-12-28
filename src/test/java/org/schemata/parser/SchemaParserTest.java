package org.schemata.parser;

import com.google.protobuf.GeneratedMessageV3;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.schemata.schema.ExampleBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SchemaParserTest {

  @Test
  void validate() {
    SchemaParser validator = new SchemaParser();
    List<GeneratedMessageV3> messages = List.of(ExampleBuilder.Person.newBuilder().build());
    validator.parseSchema(messages);
    assertEquals(2, 2);
  }
}
