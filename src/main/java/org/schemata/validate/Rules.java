package org.schemata.validate;

public enum Rules {
  SCHEMA_DESCRIPTION_EMPTY("Schema description metadata is null or empty"),
  SCHEMA_DOMAIN_EMPTY("Schema domain metadata is null or empty"),
  SCHEMA_OWNER_EMPTY("Schema owner metadata is null or empty"),
  SCHEMA_UNKNOWN_TYPE("UNKNOWN is not a valid type. It should be either ENTITY or EVENT"),
  FIELD_DESCRIPTION_EMPTY("Field description metadata is null or empty"),
  FIELD_CLASSIFICATION_EMPTY("The field marked as classified, but the classification level is missing");

  public final String errorMessage;

  Rules(String ruleMessage) {
    this.errorMessage = ruleMessage;
  }
}
