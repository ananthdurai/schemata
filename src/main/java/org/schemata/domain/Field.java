package org.schemata.domain;

import java.util.List;


public record Field(String schema, String name, String dataType, boolean isPrimitiveType, String description,
                    String comment, String seeAlso, String reference, boolean isClassified, String classificationLevel,
                    boolean isPrimaryKey, String productType, Link link, List<Depends> depends) {

  private Field(Builder builder) {
    this(builder.schema, builder.name, builder.dataType, builder.isPrimitiveType, builder.description, builder.comment,
        builder.seeAlso, builder.reference, builder.isClassified, builder.classificationLevel, builder.isPrimaryKey,
        builder.productType, builder.link, builder.depends);
  }

  public static class Builder {

    String schema;
    String name;
    String dataType;
    boolean isPrimitiveType;
    String description;
    String comment;
    String seeAlso;
    String reference;
    boolean isClassified;
    String classificationLevel;
    boolean isPrimaryKey;
    String productType;
    Link link;
    List<Depends> depends;

    public Builder(String schema, String name, String dataType, boolean isPrimitiveType) {
      this.schema = schema;
      this.name = name;
      this.dataType = dataType;
      this.isPrimitiveType = isPrimitiveType;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public Builder seeAlso(String seeAlso) {
      this.seeAlso = seeAlso;
      return this;
    }

    public Builder reference(String reference) {
      this.reference = reference;
      return this;
    }

    public Builder isClassified(boolean classified) {
      this.isClassified = classified;
      return this;
    }

    public Builder classificationLevel(String classifiedLevel) {
      this.classificationLevel = classifiedLevel;
      return this;
    }

    public Builder primaryKey(boolean primaryKey) {
      this.isPrimaryKey = primaryKey;
      return this;
    }

    public Builder productType(String productType) {
      this.productType = productType;
      return this;
    }

    public Builder link(Link link) {
      this.link = link;
      return this;
    }

    public Builder depends(List<Depends> depends) {
      this.depends = depends;
      return this;
    }

    public Field build() {
      return new Field(this);
    }
  }

  public static class Prop {
    public static final String DESC = "desc";
    public static final String DESCRIPTION = "description";
    public static final String COMMENT = "comment";
    public static final String SEE_ALSO = "see_also";
    public static final String REFERENCE = "reference";
    public static final String IS_CLASSIFIED = "is_classified";
    public static final String IS_PRIMARY_KEY = "is_primary_key";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String LINK = "link";
    public static final String DEPENDS = "depends";
    public static final String MODEL = "model";

    public static final String COLUMN = "column";
  }
}
