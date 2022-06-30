package org.schemata.provider.dbt;

import java.util.List;


public interface DbtCatalogMetadata {
  record Table(String namespace, String modelName, String dbtModelFullName, String comment, String owner) {
  }

  record Column(String name, String dataType, long index, String comment) {
  }

  record Catalog(Table table, List<Column> column) {
  }

  String TARGET_PATH = "target";
  String MANIFEST_FILE = "manifest.json";
  String CATALOG_FILE = "catalog.json";
}
