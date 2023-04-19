package org.schemata.graph;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.SupplierUtil;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaNotFoundException;


public final class SchemaGraph {

  private final DirectedWeightedMultigraph<Schema, WeightedSchemaEdge> graph =
      new DirectedWeightedMultigraph<>(SupplierUtil.createSupplier(Schema.class),
          SupplierUtil.createSupplier(WeightedSchemaEdge.class));

  private final List<Schema> schemaList;
  Map<String, Schema> schemaMap;
  private final PageRank<Schema, WeightedSchemaEdge> pageRank;

  public SchemaGraph(List<Schema> schemaList) {
    this.schemaList = schemaList;
    this.schemaMap = buildGraph();
    this.buildEdge();
    pageRank = new PageRank<>(graph);
  }

  private Map<String, Schema> buildGraph() {
    Map<String, Schema> schemaMap = new HashMap<>();
    for (Schema schema : schemaList) {
      schemaMap.put(schema.name(), schema);
      this.addVertex(schema);
    }
    return schemaMap;
  }

  private void buildEdge()
      throws SchemaNotFoundException {
    for (Schema schema : this.schemaList) {
      for (Field field : schema.fieldList()) {
        if (!field.isPrimitiveType()) {
          findVertex(field.dataType()).ifPresentOrElse(
              value -> this.addEdge(new WeightedSchemaEdge(schema, value, field)), () -> {
                throw new SchemaNotFoundException("DataType " + field.dataType() + " Not found in the graph");
              });
        }
      }
    }
  }

  private void addVertex(Schema schema) {
    graph.addVertex(schema);
  }

  private void addEdge(WeightedSchemaEdge edge) {
    if (edge == null) {
      throw new IllegalArgumentException("Edge can't be null");
    }
    graph.addEdge(edge.getSource(), edge.getTarget(), edge);
  }

  public Set<WeightedSchemaEdge> incomingEdgesOf(String vertex)
      throws SchemaNotFoundException {
    return graph.incomingEdgesOf(getSchema(vertex));
  }

  public Set<Schema> incomingVertexOf(String vertex) {
    Set<Schema> incomingSchemaSet = new HashSet<>();
    incomingEdgesOf(vertex).forEach(e -> incomingSchemaSet.add(e.getSource()));
    return incomingSchemaSet;
  }

  public Set<WeightedSchemaEdge> outgoingEdgesOf(String vertex)
      throws SchemaNotFoundException {
    return graph.outgoingEdgesOf(getSchema(vertex));
  }

  public Set<Schema> outgoingVertexOf(String vertex) {
    Set<Schema> outgoingSchemaSet = new HashSet<>();
    outgoingEdgesOf(vertex).forEach(e -> outgoingSchemaSet.add(e.getTarget()));
    return outgoingSchemaSet;
  }

  public Set<Schema> outgoingEntityVertexOf(String vertex) {
    return outgoingVertexOf(vertex).stream().filter(f -> "ENTITY".equalsIgnoreCase(f.type()))
        .collect(Collectors.toSet());
  }

  public Set<Schema> getAllEntityVertex() {
    return graph.vertexSet().stream().filter(f -> "ENTITY".equalsIgnoreCase(f.type())).collect(Collectors.toSet());
  }

  public Double getVertexPageRankScore(String vertex) {
    return pageRank.getVertexScore(getSchema(vertex));
  }

  public Double getSchemataScore(String vertex) {
    var schema = getSchema(vertex);
    double score = switch (schema.type().toUpperCase()) {
      case "ENTITY" -> computeEntityScore(vertex);
      case "EVENT" -> computeEventScore(vertex, schema.eventType());
      default -> 0.0;
    };
    return roundUp(score);
  }

  private double computeEntityScore(String vertex) {
    double totalEdges = graph.edgeSet().size();
    if (totalEdges == 0) {
      return 0.0;
    }

    double referenceEdges = referenceEdges(vertex).size();
    return 1 - ((totalEdges - referenceEdges) / totalEdges);
  }

  public Set<WeightedSchemaEdge> referenceEdges(String vertex) {
    return SetUtils.union(incomingEdgesOf(vertex), outgoingEdgesOf(vertex));
  }

  private double computeEventScore(String vertex, String eventType) {
    double score = switch (eventType) {
      case "LIFECYCLE" -> outgoingEntityVertexOf(vertex).size() > 0 ? 1.0 : 0.0;
      case "ACTIVITY", "AGGREGATED" -> computeNonLifecycleScore(vertex);
      default -> 0.0;
    };
    return score;
  }

  private double computeNonLifecycleScore(String vertex) {
    double totalVertex = getAllEntityVertex().size();
    if (totalVertex == 0) {
      return 0.0;
    }
    Set<Schema> referenceVertex =
        outgoingEntityVertexOf(vertex).stream().map(v -> outgoingEntityVertexOf(v.name())).flatMap(Collection::stream)
            .collect(Collectors.toSet());
    Set<Schema> outgoingVertex = outgoingEntityVertexOf(vertex);
    double vertexCount = SetUtils.union(referenceVertex, outgoingVertex).size();

    return 1 - ((totalVertex - vertexCount) / totalVertex);
  }

  public Schema getSchema(String vertex)
      throws SchemaNotFoundException {
    return findVertex(vertex).orElseThrow(
        () -> new SchemaNotFoundException("Vertex " + vertex + " Not found in the graph"));
  }

  public Optional<Schema> findVertex(String vertex) {
    if (StringUtils.isBlank(vertex)) {
      return Optional.empty();
    }
    if (this.schemaMap.containsKey(vertex)) {
      return Optional.of(this.schemaMap.get(vertex));
    }
    return Optional.empty();
  }

  private double roundUp(double value) {
    return new BigDecimal(value, new MathContext(3)).doubleValue();
  }
}
