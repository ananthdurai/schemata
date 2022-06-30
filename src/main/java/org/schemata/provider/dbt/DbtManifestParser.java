package org.schemata.provider.dbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaParserException;
import org.schemata.json.Json;

import static org.schemata.provider.dbt.DbtCatalogMetadata.MANIFEST_FILE;
import static org.schemata.provider.dbt.DbtCatalogMetadata.TARGET_PATH;


public class DbtManifestParser {

  public List<Schema> parse(Map<String, DbtCatalogMetadata.Catalog> catalog, String path) {
    if (catalog == null) {
      return Collections.emptyList();
    }
    try {
      var jsonParser = getManifestJsonParser(path);
      var nodes = getNodes(jsonParser);

      catalog.forEach((key, value) -> {
        var node = nodes.get(key);
        if (node != null && isModel(node.getAsJsonObject())) {
          var nodeObj = node.getAsJsonObject();
          if (getConfig(nodeObj).isPresent()) {

            var fields = extractFields(nodeObj, value);

            var config = getConfig(nodeObj).get();
            String domain = Json.getAsString(config, "domain");
            String modelType = Json.getAsString(config, "domain");
            String description = Json.getAsString(nodeObj, "description");
            String seeAlso = Json.getAsString(nodeObj, "seeAlso");
            String reference = Json.getAsString(nodeObj, "reference");
          }
        }
        System.out.println(node);
      });
    } catch (IOException e) {
      throw new SchemaParserException("Error while parsing DbtManifestParser", e);
    }
    return null;
  }

  public JsonObject getNodes(JsonElement jsonParser) {
    return Json.getAsJsonObject(jsonParser.getAsJsonObject(), "nodes")
        .orElseThrow(() -> new SchemaParserException("Error parsing DbtManifestParser: Nodes is empty"));
  }

  public JsonElement getManifestJsonParser(String path)
      throws IOException {
    Stream<String> lines = Files.lines(Paths.get(path, TARGET_PATH, MANIFEST_FILE));
    String data = lines.collect(Collectors.joining("\n"));
    return JsonParser.parseString(data);
  }

  public String getResourceType(JsonObject node) {
    return Json.getAsString(node, "resource_type");
  }

  public boolean isModel(JsonObject node) {
    return getResourceType(node).equalsIgnoreCase("model");
  }

  public Optional<JsonObject> getConfig(JsonObject node) {
    return Json.getAsJsonObject(node, "config");
  }

  public List<Field> extractFields(JsonObject nodeObj, DbtCatalogMetadata.Catalog catalog) {
    var columns = Json.getAsJsonObject(nodeObj, "columns");
    catalog.column().forEach(column -> {
      var builder = new Field.Builder(catalog.table().modelName(), column.name(), column.dataType(), true);
    });
    return null;
  }
}
