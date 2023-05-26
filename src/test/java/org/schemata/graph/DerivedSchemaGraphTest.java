package org.schemata.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;
import org.schemata.provider.dbt.DbtSchemaParser;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DerivedSchemaGraphTest {

  static SchemaGraph graph;

  @BeforeAll
  static void setUp() {
    DbtSchemaParser parser = new DbtSchemaParser();
    var schemaList = parser.getSchemaList(ResourceLoader.getDbtBasePath());
    graph = new SchemaGraph(schemaList);
  }

  @Test
  public void testGetAllFaceVertex() {
    assertEquals(1, graph.getAllFactVertex().size());
    System.out.println(graph.getAllFactVertex());
  }

}
