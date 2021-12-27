package org.schemata.validate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RulesTest {

  @Test
  public void testDescription() {
    assertEquals("Schema domain metadata is null or empty", Rules.SCHEMA_DOMAIN_EMPTY.errorMessage);
  }
}
