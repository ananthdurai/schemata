package org.schemata.parser;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.schema.SchemataBuilder;


public class SchemaParser {

  public List<Schema> parseSchema(List<GeneratedMessageV3> messages) {
    List<Schema> schemaList = new ArrayList<>();
    for (GeneratedMessageV3 message : messages) {
      Descriptors.Descriptor descriptorType = message.getDescriptorForType();
      String schemaName = descriptorType.getFullName();
      // Extract all the metadata for the fieldList
      var fieldList = extractFields(descriptorType.getFields(), schemaName);
      var schema = this.extractSchema(descriptorType, descriptorType.getFullName(), fieldList);
      schemaList.add(schema);
    }
    return schemaList;
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
        case "status" -> builder.status(Objects.toString(entry.getValue(), ""));
        case "team_channel" -> builder.teamChannel(Objects.toString(entry.getValue(), ""));
        case "alert_channel" -> builder.alertChannel(Objects.toString(entry.getValue(), ""));
      }
    }
    return builder.build();
  }

  public List<Field> extractFields(List<Descriptors.FieldDescriptor> fieldDescriptorList, String schema) {
    List<Field> fields = new ArrayList<>();

    for (Descriptors.FieldDescriptor entry : fieldDescriptorList) {
      String type = entry.getType() == Descriptors.FieldDescriptor.Type.MESSAGE ? entry.getMessageType().getFullName()
          : entry.getType().name();
      Field.Builder builder = new Field.Builder(schema, entry.getName(), type);

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
}
