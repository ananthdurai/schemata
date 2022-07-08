package org.schemata.provider.dbt;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.schemata.ResourceLoader;
import org.schemata.domain.Depends;
import org.schemata.domain.EventType;
import org.schemata.domain.Link;
import org.schemata.domain.ModelType;
import org.schemata.domain.SchemaType;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DbtManifestParserTest {
  Map<String, DbtCatalogMetadata.Catalog> catalog;
  DbtManifestParser manifestParser;

  @BeforeEach
  public void init() {
    catalog = new DbtCatalogParser().parse(ResourceLoader.getDbtBasePath());
    manifestParser = new DbtManifestParser();
  }

  @Test
  public void testParse() {
    assertDoesNotThrow(() -> manifestParser.parse(catalog, ResourceLoader.getDbtBasePath()));
  }

  @Test
  public void testSchemaTable() {
    var schemaList = manifestParser.parse(catalog, ResourceLoader.getDbtBasePath());
    assertEquals(schemaList.size(), 7);
  }

  @Test
  public void testReviewsSchema() {
    var schemaList = manifestParser.parse(catalog, ResourceLoader.getDbtBasePath());
    var schema = schemaList.stream().filter(f -> f.name().equals("model.dbtlearn.src_reviews")).toList();
    assertEquals(schema.size(), 1);
    var reviewsSchema = schemaList.get(0);
    assertEquals(reviewsSchema.domain(), "core");
    assertEquals(reviewsSchema.modelType(), ModelType.DIMENSION.name());
    assertEquals(reviewsSchema.eventType(), EventType.NONE.name());
    assertEquals(reviewsSchema.type(), SchemaType.MODEL.name());
  }

  @Test
  public void testReviewColumns() {
    var schemaList = manifestParser.parse(catalog, ResourceLoader.getDbtBasePath());
    var schema = schemaList.stream().filter(f -> f.name().equals("model.dbtlearn.src_reviews")).toList().get(0);
    var fieldList = schema.fieldList();
    assertEquals(5, fieldList.size());
    var field = fieldList.stream().filter(f -> f.name().equals("listing_id")).toList().get(0);
    assertTrue(field.isPrimaryKey());
    assertEquals(new Link("src_listings", "id"), field.link());
    List<Depends> dependsList = List.of(new Depends("listings", "id"));
    assertEquals(dependsList, field.depends());
  }
}
