package org.schemata.graph;

import com.google.protobuf.Descriptors;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.schemata.TestResourceLoader;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaNotFoundException;
import org.schemata.parser.ProtoFileDescriptorSetLoader;
import org.schemata.parser.SchemaParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SchemaGraphTest {

  static SchemaGraph graph;

  @BeforeAll
  static void setUp()
      throws IOException, Descriptors.DescriptorValidationException {
    var stream = new FileInputStream(TestResourceLoader.getDescriptorsPath());
    var protoFileDescriptorLoader = new ProtoFileDescriptorSetLoader(stream);
    var parser = new SchemaParser();
    var schemaList = parser.parse(protoFileDescriptorLoader.loadDescriptors());
    graph = new SchemaGraph(schemaList);
  }

  @Test
  public void testWithInvalidSchema() {
    Assertions.assertThrows(SchemaNotFoundException.class, () -> graph.getSchema("User"),
        "Schema not found was expected");
  }

  @Test
  public void testWithValidSchema() {
    assertEquals("org.schemata.schema.UserEvent", graph.getSchema("org.schemata.schema.UserEvent").name());
  }

  @Test
  public void testIncomingEdges() {
    var incomingEdges = graph.incomingEdgesOf("org.schemata.schema.User");
    var expectedEdges = Set.of(newUserEdge("org.schemata.schema.UserEvent", "previous_user_state"),
        newUserEdge("org.schemata.schema.UserEvent", "current_user_state"),
        newUserEdge("org.schemata.schema.UserActivityEvent", "user"),
        newUserEdge("org.schemata.schema.UserActivityAggregate", "user"));
    assertEquals(4, incomingEdges.size());
    var actualEdges = incomingEdges.stream().map(WeightedSchemaEdge::summaryPrint).collect(Collectors.toSet());
    assertTrue(SetUtils.isEqualSet(expectedEdges, actualEdges));
  }

  @Test
  public void testIncomingVertex() {
    var incomingSchemaSet = graph.incomingVertexOf("org.schemata.schema.User");
    var expectedVertex = Set.of("org.schemata.schema.UserActivityAggregate", "org.schemata.schema.UserActivityEvent",
        "org.schemata.schema.UserEvent");
    var actualVertex = incomingSchemaSet.stream().map(Schema::name).collect(Collectors.toSet());
    assertEquals(3, incomingSchemaSet.size());
    assertTrue(SetUtils.isEqualSet(expectedVertex, actualVertex));
  }

  @Test
  public void testOutgoingEdges() {
    var outgoingEdges = graph.outgoingEdgesOf("org.schemata.schema.UserActivityAggregate");
    var expectedEdges = Set.of(newUserActivityAggregateEdge("org.schemata.schema.User", "user"),
        newUserActivityAggregateEdge("org.schemata.schema.Product", "product"));
    assertEquals(2, outgoingEdges.size());
    var actualEdges = outgoingEdges.stream().map(WeightedSchemaEdge::summaryPrint).collect(Collectors.toSet());
    assertTrue(SetUtils.isEqualSet(expectedEdges, actualEdges));
  }

  @Test
  public void testOutgoingVertex() {
    var outgoingSchemaSet = graph.outgoingVertexOf("org.schemata.schema.UserActivityAggregate");
    var expectedVertex = Set.of("org.schemata.schema.Product", "org.schemata.schema.User");
    var actualVertex = outgoingSchemaSet.stream().map(Schema::name).collect(Collectors.toSet());
    assertEquals(2, outgoingSchemaSet.size());
    assertTrue(SetUtils.isEqualSet(expectedVertex, actualVertex));
  }

  @Test
  public void testOutgoingEntityVertex() {
    var outgoingSchemaSet = graph.outgoingEntityVertexOf("org.schemata.schema.UserActivityAggregate");
    for (var schema : outgoingSchemaSet) {
      System.out.println(schema.name());
    }
  }

  @Test
  public void testPageRankScore() {
    assertTrue(graph.getVertexPageRankScore("org.schemata.schema.Product") > graph.getVertexPageRankScore(
        "org.schemata.schema.User"));
  }

  @Test
  public void testSchemataScore() {
    assertTrue(
        graph.getSchemataScore("org.schemata.schema.Product") > graph.getSchemataScore("org.schemata.schema.User"));
    assertTrue(graph.getSchemataScore("org.schemata.schema.CampaignProductTrackerEvent") > graph.getSchemataScore(
        "org.schemata.schema.CampaignCategoryTrackerEvent"));
  }

  private String newUserEdge(String source, String edge) {
    return newEdge(source, "org.schemata.schema.User", edge);
  }

  private String newUserActivityAggregateEdge(String target, String edge) {
    return newEdge("org.schemata.schema.UserActivityAggregate", target, edge);
  }

  private String newEdge(String source, String target, String edge) {
    Field field = new Field.Builder(source, edge, target, false).build();
    return new WeightedSchemaEdge(graph.getSchema(source), graph.getSchema(target), field).summaryPrint();
  }
}