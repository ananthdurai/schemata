package org.schemata.validate;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SchemaValidatorTest {

  @Test
  public void testEntityWithMissedPrimaryKey() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.description("Schema Description");
    builder.owner("Growth");
    builder.domain("Core");
    builder.type("ENTITY");
    assertEquals(Status.ERROR, new SchemaValidator().apply(builder.build()).status());
  }

  @Test
  public void testEntityWithValidPrimaryKey() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    fieldBuilder.primaryKey(true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.description("Schema Description");
    builder.owner("Growth");
    builder.domain("Core");
    builder.type("ENTITY");
    assertEquals(Status.SUCCESS, new SchemaValidator().apply(builder.build()).status());
  }

  @Test
  public void testValidEvent() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.description("Schema Description");
    builder.owner("Growth");
    builder.domain("Core");
    builder.type("EVENT");
    assertEquals(Status.SUCCESS, new SchemaValidator().apply(builder.build()).status());
  }

  @Test
  public void testWithEmptyDescriptor() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.owner("Growth");
    builder.domain("Core");
    builder.type("EVENT");
    assertEquals(Status.ERROR, new SchemaValidator().apply(builder.build()).status());
  }

  @Test
  public void testWithEmptyOwner() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.description("Schema Description");
    builder.domain("Core");
    builder.type("EVENT");
    assertEquals(Status.ERROR, new SchemaValidator().apply(builder.build()).status());
  }

  @Test
  public void testWithEmptyDomain() {
    Field.Builder fieldBuilder = new Field.Builder("SchemaName", "FieldName", "STRING", true);
    Schema.Builder builder = new Schema.Builder("SchemaName", List.of(fieldBuilder.build()));
    builder.description("Schema Description");
    builder.owner("Growth");
    builder.type("EVENT");
    assertEquals(Status.ERROR, new SchemaValidator().apply(builder.build()).status());
  }
}
