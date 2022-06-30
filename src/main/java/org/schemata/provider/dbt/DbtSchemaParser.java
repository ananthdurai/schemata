package org.schemata.provider.dbt;

import java.util.List;
import org.schemata.domain.Schema;
import org.schemata.provider.SchemaParser;


/**
 * Parse the dbt generated catalog.json and manifest.json to gather metadata
 *
 * Steps:
 * ======
 * 1. Parse the catalog.json and gather the list of models, columns and types & index
 * 2. Parse manifest.json for each model and gather additional metadata
 */
public class DbtSchemaParser implements SchemaParser {

  @Override
  public List<Schema> getSchemaList(String path) {
    var dbtCatalogParser = new DbtCatalogParser();
    var catalog = dbtCatalogParser.parse(path);
    return new DbtManifestParser().parse(catalog, path);
  }
}
