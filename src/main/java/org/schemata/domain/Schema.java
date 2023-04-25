package org.schemata.domain;

import java.util.List;


public record Schema(String name, String description, String comment, String seeAlso, String reference, String owner,
                     String domain, String status, String type, String eventType, String modelType, String teamChannel,
                     String alertChannel, String complianceOwner, String complianceChannel,List<Subscribers> subscribersList, List<Field> fieldList) {

  private Schema(Builder builder) {
    this(builder.name, builder.description, builder.comment, builder.seeAlso, builder.reference, builder.owner,
        builder.domain, builder.status, builder.schemaType.name(), builder.eventType.name(), builder.modelType.name(),
        builder.teamChannel, builder.alertChannel, builder.complianceOwner, builder.complianceChannel,
        builder.subscribersList, builder.fieldList);
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
    SchemaType schemaType;
    EventType eventType;
    ModelType modelType;
    String teamChannel;
    String alertChannel;
    String complianceOwner;
    String complianceChannel;
    List<Field> fieldList;
    List<Subscribers> subscribersList;

    public Builder(String name, List<Field> fieldList) {
      this.name = name;
      this.fieldList = fieldList;
      this.eventType = EventType.NONE;
      this.modelType = ModelType.NONE;
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

    public Builder schemaType(String schemaTypeValue) {
      this.schemaType = SchemaType.get(schemaTypeValue);
      return this;
    }

    public Builder eventType(String eventTypeValue) {
      this.eventType = EventType.get(eventTypeValue);
      return this;
    }

    public Builder modelType(String modelTypeValue) {
      this.modelType = ModelType.get(modelTypeValue);
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

    public Builder subscribersList(List<Subscribers> subscribersList) {
      this.subscribersList = subscribersList;
      return this;
    }

    public Schema build() {
      return new Schema(this);
    }
  }

  public static final class Prop {

    public static final String DESC = "desc";

    public static final String DESCRIPTION = "description";
    public static final String COMMENT = "comment";
    public static final String SEE_ALSO = "see_also";
    public static final String REFERENCE = "reference";
    public static final String OWNER = "owner";
    public static final String DOMAIN = "domain";
    public static final String STATUS = "status";
    public static final String SCHEMA_TYPE = "schema_type";
    public static final String EVENT_TYPE = "event_type";
    public static final String MODEL_TYPE = "model_type";
    public static final String TEAM_CHANNEL = "team_channel";
    public static final String ALERT_CHANNEL = "alert_channel";
    public static final String COMPLIANCE_OWNER = "compliance_owner";
    public static final String COMPLIANCE_CHANNEL = "compliance_channel";
  }
}
