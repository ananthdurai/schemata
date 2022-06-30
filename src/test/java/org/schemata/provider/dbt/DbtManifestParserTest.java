package org.schemata.provider.dbt;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;


public class DbtManifestParserTest {

  Map<String, DbtCatalogMetadata.Catalog> catalog;

  @BeforeEach
  public void init() {
    var parser = new DbtCatalogParser();
    catalog = parser.parse(ResourceLoader.getDbtBasePath());
  }

  @Test
  public void testParse() {
    System.out.println(new DbtManifestParser().parse(catalog, ResourceLoader.getDbtBasePath()));
  }
}
