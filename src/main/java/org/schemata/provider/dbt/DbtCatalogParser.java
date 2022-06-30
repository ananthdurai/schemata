package org.schemata.provider.dbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.schemata.exception.SchemaParserException;
import org.schemata.json.Json;

import static org.schemata.provider.dbt.DbtCatalogMetadata.CATALOG_FILE;
import static org.schemata.provider.dbt.DbtCatalogMetadata.TARGET_PATH;


public class DbtCatalogParser {

  public Map<String, DbtCatalogMetadata.Catalog> parse(String path) {
    Map<String, DbtCatalogMetadata.Catalog> catalogMap = new HashMap<>();

    var jsonParser = getCatalogJsonParser(path);
    var nodes = getNodes(jsonParser);
    nodes.entrySet().forEach(entry -> {
      var table = extractTable(entry.getKey(), entry.getValue());
      var columnList = extractColumn(entry.getValue());
      catalogMap.put(entry.getKey(), new DbtCatalogMetadata.Catalog(table, columnList));
    });
    return catalogMap;
  }

  public JsonElement getCatalogJsonParser(String path) {
    try (Stream<String> lines = Files.lines(Paths.get(path, TARGET_PATH, CATALOG_FILE))) {
      String data = lines.collect(Collectors.joining("\n"));
      return JsonParser.parseString(data);
    } catch (IOException e) {
      throw new SchemaParserException("Error while parsing getCatalogJsonParser:", e);
    }
  }

  public List<DbtCatalogMetadata.Column> extractColumn(JsonElement element) {
    var columns = getColumnJsonElement(element);
    List<DbtCatalogMetadata.Column> fieldList = new ArrayList<>();
    columns.entrySet().forEach(column -> {
      var columnObj = column.getValue().getAsJsonObject();
      fieldList.add(new DbtCatalogMetadata.Column(getColumnName(column), getDataType(columnObj),
          getColumnIndex(columnObj), getColumnComment(columnObj)));
    });
    return fieldList;
  }

  public JsonObject getColumnJsonElement(JsonElement element) {
    return Json.getAsJsonObject(element.getAsJsonObject(), "columns")
        .orElseThrow(() -> new SchemaParserException("Error parsing dbt catalog: columns is empty"));
  }

  public String getColumnComment(JsonObject columnObj) {
    return Json.getAsString(columnObj, "comment");
  }

  public Long getColumnIndex(JsonObject columnObj) {
    return Json.getAsLong(columnObj, "index");
  }

  public String getDataType(JsonObject columnObj) {
    return Json.getAsString(columnObj, "type");
  }

  public String getColumnName(Map.Entry<String, JsonElement> column) {
    return column.getKey().toLowerCase();
  }

  public DbtCatalogMetadata.Table extractTable(String modelName, JsonElement element) {
    var metadata = getMetadata(element);
    return new DbtCatalogMetadata.Table(getNamespace(metadata), getModelName(metadata), modelName,
        getComment(metadata), getOwner(metadata));
  }

  public JsonObject getMetadata(JsonElement element) {
    return Json.getAsJsonObject(element.getAsJsonObject(), "metadata")
        .orElseThrow(() -> new SchemaParserException("Error parsing dbt catalog: Metadata Object is Empty"));
  }

  public JsonObject getNodes(JsonElement jsonParser) {
    return Json.getAsJsonObject(jsonParser.getAsJsonObject(), "nodes")
        .orElseThrow(() -> new SchemaParserException("Error parsing dbt catalog: Nodes is empty"));
  }

  public String getOwner(JsonObject metadata) {
    return Json.getAsString(metadata, "owner");
  }

  public String getComment(JsonObject metadata) {
    return Json.getAsString(metadata, "comment");
  }

  public String getModelName(JsonObject metadata) {
    return Json.getAsString(metadata, "name");
  }

  public String getNamespace(JsonObject metadata) {
    return Json.getAsString(metadata, "schema");
  }
}
