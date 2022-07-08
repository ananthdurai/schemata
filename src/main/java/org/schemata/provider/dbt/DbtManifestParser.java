package org.schemata.provider.dbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.schemata.domain.Depends;
import org.schemata.domain.Field;
import org.schemata.domain.Link;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaParserException;
import org.schemata.json.Json;

import static org.schemata.provider.dbt.DbtCatalogMetadata.MANIFEST_FILE;


public class DbtManifestParser {

  public List<Schema> parse(Map<String, DbtCatalogMetadata.Catalog> catalog, String path) {
    if (catalog == null) {
      return Collections.emptyList();
    }

    List<Schema> schemaList = new ArrayList<>();

    try {
      var jsonParser = getManifestJsonParser(path);
      var nodes = getNodes(jsonParser);

      catalog.forEach((key, value) -> {
        var node = nodes.get(key);
        if (node != null && isModel(node.getAsJsonObject())) {
          var nodeObj = node.getAsJsonObject();
          if (getConfig(nodeObj).isPresent()) {

            var fields = extractFields(nodeObj, value);
            var schema = new Schema.Builder(key, fields);

            var config = getConfig(nodeObj).get();

            var builder = schema.domain(Json.getAsString(config, Schema.Prop.DOMAIN))
                .schemaType("model") // We parse only the dbt model type. so all dbt we set it as model
                .modelType(Json.getAsString(config, Schema.Prop.MODEL_TYPE))
                .description(Json.getAsString(nodeObj, Schema.Prop.DESCRIPTION))
                .reference(Json.getAsString(config, Schema.Prop.REFERENCE))
                .seeAlso(Json.getAsString(config, Schema.Prop.SEE_ALSO))
                .comment(value.table().comment())
                .owner(value.table().owner())
                .status(Json.getAsString(config, Schema.Prop.STATUS))
                .alertChannel(Json.getAsString(config, Schema.Prop.ALERT_CHANNEL))
                .teamChannel(Json.getAsString(config, Schema.Prop.TEAM_CHANNEL))
                .complianceChannel(Json.getAsString(config, Schema.Prop.COMPLIANCE_CHANNEL))
                .complianceOwner(Json.getAsString(config, Schema.Prop.COMPLIANCE_OWNER));
            schemaList.add(builder.build());
          }
        }
      });
    } catch (IOException e) {
      throw new SchemaParserException("Error while parsing DbtManifestParser", e);
    }
    return schemaList;
  }

  public JsonObject getNodes(JsonElement jsonParser) {
    return Json.getAsJsonObject(jsonParser.getAsJsonObject(), "nodes")
        .orElseThrow(() -> new SchemaParserException("Error parsing DbtManifestParser: Nodes is empty"));
  }

  public JsonElement getManifestJsonParser(String path)
      throws IOException {
    Stream<String> lines = Files.lines(Paths.get(path, MANIFEST_FILE));
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
    if (columns.isEmpty()) {
      return extractFieldsWithManifestMetadataNotDetected(catalog);
    }
    return enrichColumnMetadataWithManifest(catalog, columns.get());
  }

  private List<Field> enrichColumnMetadataWithManifest(DbtCatalogMetadata.Catalog catalog, JsonObject columns) {
    List<Field> fieldList = new ArrayList<>();
    catalog.column().forEach(column -> {
      var columnObj = columns.getAsJsonObject(column.name());
      var builder =
          new Field.Builder(catalog.table().modelName(), column.name(), column.dataType(), true);

      if (isMetaObjExist(columnObj)) {
        var metaObj = columnObj.getAsJsonObject("meta");
        builder.description(Json.getAsString(metaObj, Field.Prop.DESCRIPTION))
            .link(getFieldLink(metaObj))
            .depends(getDepends(metaObj))
            .primaryKey(Json.getAsBoolean(metaObj, Field.Prop.IS_PRIMARY_KEY));
      }
      fieldList.add(builder.build());
    });
    return fieldList;
  }

  public boolean isMetaObjExist(JsonObject columnObj) {
    return columnObj != null && columnObj.getAsJsonObject("meta") != null;
  }

  private Link getFieldLink(JsonObject obj) {
    var linkObj = Json.getAsJsonObject(obj, Field.Prop.LINK);

    if (linkObj.isEmpty()) {
      return null;
    }
    var linkPropObj = linkObj.get();
    return new Link(Json.getAsString(linkPropObj, Field.Prop.MODEL), Json.getAsString(linkPropObj, Field.Prop.COLUMN));
  }

  private List<Depends> getDepends(JsonObject metaObj) {
    List<Depends> dependsList = new ArrayList<>();
    var obj = Json.getAsJsonArray(metaObj, Field.Prop.DEPENDS);
    if (obj.isEmpty()) {
      return dependsList;
    }
    for (JsonElement depends : obj.get()) {
      dependsList.add(new Depends(Json.getAsString(depends.getAsJsonObject(), Field.Prop.MODEL),
          Json.getAsString(depends.getAsJsonObject(), Field.Prop.COLUMN)));
    }
    return dependsList;
  }

  public List<Field> extractFieldsWithManifestMetadataNotDetected(DbtCatalogMetadata.Catalog catalog) {
    List<Field> fieldList = new ArrayList<>();
    catalog.column().forEach(column -> {
      fieldList.add(new Field.Builder(catalog.table().modelName(), column.name(), column.dataType(), true)
          .build());
    });
    return fieldList;
  }
}
