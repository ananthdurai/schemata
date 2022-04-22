package org.schemata.domain;

public record Field(String schema, String name, String dataType, boolean isPrimitiveType, String description,
                    String comment, String seeAlso, String reference, boolean isClassified, String classificationLevel,
                    boolean isPrimaryKey, String productType) {

  private Field(Builder builder) {
    this(builder.schema, builder.name, builder.dataType, builder.isPrimitiveType, builder.description, builder.comment,
        builder.seeAlso, builder.reference, builder.isClassified, builder.classificationLevel, builder.isPrimaryKey,
        builder.productType);
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
      isClassified = classified;
      return this;
    }

    public Builder classificationLevel(String classifiedLevel) {
      this.classificationLevel = classifiedLevel;
      return this;
    }

    public Builder primaryKey(boolean primaryKey) {
      isPrimaryKey = primaryKey;
      return this;
    }

    public Builder productType(String productType) {
      this.productType = productType;
      return this;
    }

    public Field build() {
      return new Field(this);
    }
  }

  public static class Prop {
    public static final String DESC = "desc";
    public static final String COMMENT = "comment";
    public static final String SEE_ALSO = "see_also";
    public static final String REFERENCE = "reference";
    public static final String IS_CLASSIFIED = "is_classified";
    public static final String IS_PRIMARY_KEY = "is_primary_key";
    public static final String PRODUCT_TYPE = "product_type";
  }
}
