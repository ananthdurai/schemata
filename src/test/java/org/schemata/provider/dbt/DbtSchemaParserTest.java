package org.schemata.provider.dbt;

import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DbtSchemaParserTest {

  @Test
  public void testSchemataList() {
    DbtSchemaParser parser = new DbtSchemaParser();
    var schemaList = parser.getSchemaList(ResourceLoader.getDbtBasePath());
    var expectedSize = 7;
    assertEquals(expectedSize, schemaList.size());
  }
}
