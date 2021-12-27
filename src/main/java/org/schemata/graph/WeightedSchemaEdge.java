package org.schemata.graph;

import org.jgrapht.graph.DefaultEdge;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;


public class WeightedSchemaEdge extends DefaultEdge {

  private static Double DEFAULT_WEIGHT = 1.0;
  Schema source;
  Schema target;
  Field edgeField;
  double weight; // Set default weight == 1

  public WeightedSchemaEdge(Schema source, Schema target, Field edgeField) {
    this(source, target, edgeField, DEFAULT_WEIGHT);
  }

  public WeightedSchemaEdge(Schema source, Schema target, Field edgeField, double weight) {
    this.source = source;
    this.target = target;
    this.edgeField = edgeField;
    this.weight = weight;
  }

  @Override
  public Schema getSource() {
    return source;
  }

  @Override
  public Schema getTarget() {
    return target;
  }

  public Field getEdgeField() {
    return edgeField;
  }

  public double getWeight() {
    return weight;
  }
}
