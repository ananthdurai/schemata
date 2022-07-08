package org.schemata.provider.dbt;

import com.google.gson.JsonElement;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;
import org.schemata.exception.SchemaParserException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DbtCatalogParserTest {

  JsonElement element;
  DbtCatalogParser parser;

  @BeforeEach
  public void init() {
    parser = new DbtCatalogParser();
    element = parser.getCatalogJsonParser(ResourceLoader.getDbtBasePath());
  }

  @Test
  public void testGetCatalogParserWithInvalidPath() {
    assertThrows(SchemaParserException.class,
        () -> new DbtCatalogParser().getCatalogJsonParser(ResourceLoader.getInvalidDbtBasePath()));
  }

  @Test
  public void testGetCatalogParser() {
    assertDoesNotThrow(() -> new DbtCatalogParser().getCatalogJsonParser(ResourceLoader.getDbtBasePath()));
  }

  @Test
  public void testGetNodes() {
    assertTrue(parser.getNodes(element).isJsonObject());
  }

  @Test
  public void testExtractTable() {
    var nodes = parser.getNodes(element);
    String modelName = "model.dbtlearn.fct_reviews";
    var expected = new DbtCatalogMetadata.Table("dev", "fct_reviews",
        "model.dbtlearn.fct_reviews", "", "transform");
    assertEquals(expected, parser.extractTable(modelName, nodes.get(modelName)));
  }

  @Test
  public void testExtractColumn() {
    var nodes = parser.getNodes(element);
    String modelName = "model.dbtlearn.fct_reviews";

    var element = nodes.get(modelName);

    var expected = List.of(new DbtCatalogMetadata.Column("listing_id", "number", 1, ""),
        new DbtCatalogMetadata.Column("review_date", "timestamp_ntz", 2, ""),
        new DbtCatalogMetadata.Column("reviewer_name", "text", 3, ""),
        new DbtCatalogMetadata.Column("review_text", "text", 4, ""),
        new DbtCatalogMetadata.Column("review_sentiment", "text", 5, "")
    );

    assertEquals(expected, parser.extractColumn(element));
  }
}
