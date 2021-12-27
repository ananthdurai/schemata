package org.schemata.domain;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.schemata.exception.InvalidSchemaException;
import org.schemata.schema.SchemataBuilder;


public record Schema(String name, String description, String comment, String seeAlso, String reference, String owner,
                     String domain, String status, String type, String teamChannel, String alertChannel,
                     List<Field> fieldList) {

  private Schema(Builder builder) {
    this(builder.name, builder.description, builder.comment, builder.seeAlso, builder.reference, builder.owner,
        builder.domain, builder.status, builder.type.name(), builder.teamChannel, builder.alertChannel, builder.fieldList);
  }

  public void validate()
      throws InvalidSchemaException {
    if (StringUtils.isBlank(this.description)) {
      throw new InvalidSchemaException("metadata description is null or empty");
    }
    if (StringUtils.isBlank(this.owner)) {
      throw new InvalidSchemaException("metadata owner is null or empty");
    }
    if (StringUtils.isBlank(this.domain)) {
      throw new InvalidSchemaException("metadata domain is null or empty");
    }
    if (SchemataBuilder.Type.UNKNOWN.name().equals(this.type)) {
      throw new InvalidSchemaException("Type can't be unknown. It should be either ENTITY or EVENT");
    }
  }

  public static class Builder {
    String name;
    String description;
    String comment;
    String seeAlso;
    String reference;
    String owner;
    String domain;
    String status;
    SchemataBuilder.Type type;
    String teamChannel;
    String alertChannel;
    List<Field> fieldList;

    public Builder(String name, List<Field> fieldList) {
      this.name = name;
      this.fieldList = fieldList;
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

    public Builder owner(String owner) {
      this.owner = owner;
      return this;
    }

    public Builder domain(String domain) {
      this.domain = domain;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder type(String typeValue) {
      this.type = SchemataBuilder.Type.valueOf(typeValue);
      return this;
    }

    public Builder teamChannel(String teamChannel) {
      this.teamChannel = teamChannel;
      return this;
    }

    public Builder alertChannel(String alertChannel) {
      this.alertChannel = alertChannel;
      return this;
    }

    public Schema build()
        throws InvalidSchemaException {
      return new Schema(this);
    }
  }
}
