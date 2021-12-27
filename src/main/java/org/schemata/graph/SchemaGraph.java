package org.schemata.graph;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.SupplierUtil;
import org.schemata.domain.Schema;


public final class SchemaGraph {

  private final DirectedWeightedMultigraph<Schema, WeightedSchemaEdge> graph =
      new DirectedWeightedMultigraph<>(SupplierUtil.createSupplier(Schema.class),
          SupplierUtil.createSupplier(WeightedSchemaEdge.class));

  private List<Schema> schemaList;

  public SchemaGraph(List<Schema> schemaList) {
    this.schemaList = schemaList;
  }

  private void buildGraph() {
    // Write code to build graph
  }

  private void addVertex(Schema schema) {
    graph.addVertex(schema);
  }

  private void addEdge(WeightedSchemaEdge edge) {
    if (edge == null) {
      throw new IllegalArgumentException("Edge can't be null");
    }
    if (!graph.containsVertex(edge.getSource())) {
      this.addVertex(edge.getSource());
    }

    if (!graph.containsVertex(edge.getTarget())) {
      this.addVertex(edge.getTarget());
    }
    graph.addEdge(edge.getSource(), edge.getTarget(), edge);
  }

  public List<Schema> ingress(String vertex) {
    return null;
  }

  public List<Schema> egress(String vertex) {
    return null;
  }

  public Double score(String vertex) {
    return 0.0;
  }

  public Optional<Schema> getSchema(String vertex) {
    if (StringUtils.isBlank(vertex)) {
      return Optional.empty();
    }
    for (Schema schema : graph.vertexSet()) {
      if (vertex.equalsIgnoreCase(schema.name())) {
        return Optional.of(schema);
      }
    }
    return Optional.empty();
  }

  public boolean exist(String vertex) {
    return this.getSchema(vertex).isPresent();
  }
}
