package org.schemata.graph;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jgrapht.graph.DefaultEdge;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;


public class WeightedSchemaEdge extends DefaultEdge {

  private static final Double DEFAULT_WEIGHT = 1.0;
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

  @Override
  public String toString() {
    return "WeightedSchemaEdge{" + "source=" + source + ", target=" + target + ", edgeField=" + edgeField + ", weight="
        + weight + '}';
  }

  public String summaryPrint() {
    return "WeightedSchemaEdge{" + "source=" + source.name() + ", target=" + target.name() + ", edgeField="
        + edgeField.name() + ", weight=" + weight + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WeightedSchemaEdge that = (WeightedSchemaEdge) o;

    return new EqualsBuilder().append(weight, that.weight).append(source, that.source).append(target, that.target)
        .append(edgeField, that.edgeField).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(source).append(target).append(edgeField).append(weight).toHashCode();
  }
}
