package org.schemata.provider.dbt;

import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;


public class DbtSchemaParserTest {

  @Test
  public void testMe() {
    DbtSchemaParser parser = new DbtSchemaParser();
    parser.getSchemaList(ResourceLoader.getDbtBasePath());
  }
}
