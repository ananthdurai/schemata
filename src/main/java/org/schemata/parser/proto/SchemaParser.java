package org.schemata.parser.proto;

import com.google.protobuf.Descriptors;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.schema.SchemataBuilder;

import java.util.*;


public class SchemaParser {
  private final boolean debug = false;

  private static final Set<String> INCLUDED_PRIMITIVE_TYPES = Set.of("google.protobuf.Timestamp");

  public List<Schema> parse(List<Descriptors.Descriptor> descriptors) {
    return descriptors
        .stream()
        .filter(this::isAnnotated)
        .map(this::parseSingleSchema)
        .toList();
  }

  public Schema parseSingleSchema(Descriptors.Descriptor descriptor) {
    String schemaName = descriptor.getFullName();
    // Extract all the metadata for the fieldList
    var fieldList = extractFields(descriptor.getFields(), schemaName);
    return extractSchema(descriptor, descriptor.getFullName(), fieldList);
  }

  public Schema extractSchema(Descriptors.Descriptor descriptorType, String schema, List<Field> fieldList) {
    Schema.Builder builder = new Schema.Builder(schema, fieldList);
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : descriptorType.getOptions().getAllFields().entrySet()) {

      switch (entry.getKey().getName()) {
        case "message_core" -> {
          SchemataBuilder.CoreMetadata coreMetadata = (SchemataBuilder.CoreMetadata) entry.getValue();
          builder.description(coreMetadata.getDescription());
          builder.comment(coreMetadata.getComment());
          builder.seeAlso(coreMetadata.getSeeAlso());
          builder.reference(coreMetadata.getReference());
        }
        case "owner" -> builder.owner(Objects.toString(entry.getValue(), ""));
        case "domain" -> builder.domain(Objects.toString(entry.getValue(), ""));
        case "type" -> builder.type(entry.getValue().toString());
        case "event_type" -> builder.eventType(entry.getValue().toString());
        case "status" -> builder.status(Objects.toString(entry.getValue(), ""));
        case "team_channel" -> builder.teamChannel(Objects.toString(entry.getValue(), ""));
        case "alert_channel" -> builder.alertChannel(Objects.toString(entry.getValue(), ""));
        case "compliance_owner" -> builder.complianceOwner(Objects.toString(entry.getValue(), ""));
        case "compliance_channel" -> builder.complianceChannel(Objects.toString(entry.getValue(), ""));
      }
    }
    return builder.build();
  }

  public List<Field> extractFields(List<Descriptors.FieldDescriptor> fieldDescriptorList, String schema) {
    List<Field> fields = new ArrayList<>();

    for (Descriptors.FieldDescriptor entry : fieldDescriptorList) {
      String type = entry.getType() == Descriptors.FieldDescriptor.Type.MESSAGE ? entry.getMessageType().getFullName()
          : entry.getType().name();
      Field.Builder builder = new Field.Builder(schema, entry.getName(), type, isPrimitiveType(entry.getType(), type));

      for (Map.Entry<Descriptors.FieldDescriptor, Object> fieldEntry : entry.getOptions().getAllFields().entrySet()) {
        switch (fieldEntry.getKey().getName()) {
          case "field_core" -> {
            SchemataBuilder.CoreMetadata coreMetadata = (SchemataBuilder.CoreMetadata) fieldEntry.getValue();
            builder.description(coreMetadata.getDescription());
            builder.comment(coreMetadata.getComment());
            builder.seeAlso(coreMetadata.getSeeAlso());
            builder.reference(coreMetadata.getReference());
          }
          case "is_classified" -> builder.isClassified(
              fieldEntry.getValue() != null && Boolean.parseBoolean(fieldEntry.getValue().toString()));
          case "classification_level" -> builder.classificationLevel(Objects.toString(fieldEntry.getValue(), ""));
          case "product_type" -> builder.productType(Objects.toString(fieldEntry.getValue(), ""));
          case "is_primary_key" -> builder.primaryKey(
              fieldEntry.getValue() != null && Boolean.parseBoolean(fieldEntry.getValue().toString()));
        }
      }
      fields.add(builder.build());
    }

    return fields;
  }

  private boolean isAnnotated(Descriptors.Descriptor descriptor) {
    return !descriptor.getOptions().getExtension(org.schemata.schema.SchemataBuilder.type)
        .equals(SchemataBuilder.Type.UNKNOWN);
  }

  private boolean isPrimitiveType(Descriptors.FieldDescriptor.Type type, String typeName) {
    return type != Descriptors.FieldDescriptor.Type.MESSAGE || INCLUDED_PRIMITIVE_TYPES.contains(typeName);
  }

  private void debug(String message, Object... params) {
    if (debug) {
      System.err.printf(message + "\n", params);
    }
  }
}
