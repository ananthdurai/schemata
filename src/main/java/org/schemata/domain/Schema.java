package org.schemata.domain;

import java.util.List;
import org.schemata.schema.SchemataBuilder;


public record Schema(String name, String description, String comment, String seeAlso, String reference, String owner,
                     String domain, String status, String type, String eventType, String teamChannel,
                     String alertChannel, String complianceOwner, String complianceChannel, List<Field> fieldList) {

  private Schema(Builder builder) {
    this(builder.name, builder.description, builder.comment, builder.seeAlso, builder.reference, builder.owner,
        builder.domain, builder.status, builder.type.name(), builder.eventType.name(), builder.teamChannel,
        builder.alertChannel, builder.complianceOwner, builder.complianceChannel, builder.fieldList);
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
    SchemataBuilder.EventType eventType;
    String teamChannel;
    String alertChannel;
    String complianceOwner;
    String complianceChannel;
    List<Field> fieldList;

    public Builder(String name, List<Field> fieldList) {
      this.name = name;
      this.fieldList = fieldList;
      this.eventType = SchemataBuilder.EventType.NONE;
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

    public Builder eventType(String eventTypeValue) {
      this.eventType = SchemataBuilder.EventType.valueOf(eventTypeValue);
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

    public Builder complianceOwner(String complianceOwner) {
      this.complianceOwner = complianceOwner;
      return this;
    }

    public Builder complianceChannel(String complianceChannel) {
      this.complianceChannel = complianceChannel;
      return this;
    }

    public Schema build() {
      return new Schema(this);
    }
  }
}
