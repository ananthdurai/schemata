package org.schemata.validate;

import org.junit.jupiter.api.Test;
import org.schemata.domain.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FieldValidatorTest {

  @Test
  public void testWithEmptyDescriptor() {
    Field.Builder builder = new Field.Builder("TestSchema", "TestField", "STRING");
    builder.isClassified(true);
    builder.classificationLevel("LEVEL3");
    var result = new FieldValidator().apply(builder.build());
    assertEquals(Status.ERROR, result.status());
  }

  @Test
  public void testWithEmptyClassificationLevel() {
    Field.Builder builder = new Field.Builder("TestSchema", "TestField", "STRING");
    builder.isClassified(true);
    builder.description("Field Description");
    var result = new FieldValidator().apply(builder.build());
    assertEquals(Status.ERROR, result.status());
  }

  @Test
  public void testSuccessStatus() {
    Field.Builder builder = new Field.Builder("TestSchema", "TestField", "STRING");
    builder.isClassified(true);
    builder.description("Field Description");
    builder.classificationLevel("LEVEL3");
    var result = new FieldValidator().apply(builder.build());
    assertEquals(Status.SUCCESS, result.status());
  }

  @Test
  public void testSuccessStatusForNonClassifiedField() {
    Field.Builder builder = new Field.Builder("TestSchema", "TestField", "STRING");
    builder.description("Field Description");
    var result = new FieldValidator().apply(builder.build());
    assertEquals(Status.SUCCESS, result.status());
  }
}
