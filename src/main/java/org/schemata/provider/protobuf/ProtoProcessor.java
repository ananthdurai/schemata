package org.schemata.provider.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Value;
import org.schemata.domain.Constraints;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.domain.Subscribers;
import org.schemata.schema.SchemataBuilder;
import org.schemata.schema.SchemataConstraintsBuilder;
import org.schemata.schema.SchemataSubscribersBuilder;


import java.util.*;
import java.util.stream.Collectors;


public class ProtoProcessor {

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
                case "schema_type" -> builder.schemaType(entry.getValue().toString());
                case "event_type" -> builder.eventType(entry.getValue().toString());
                case "status" -> builder.status(Objects.toString(entry.getValue(), ""));
                case "team_channel" -> builder.teamChannel(Objects.toString(entry.getValue(), ""));
                case "alert_channel" -> builder.alertChannel(Objects.toString(entry.getValue(), ""));
                case "compliance_owner" -> builder.complianceOwner(Objects.toString(entry.getValue(), ""));
                case "compliance_channel" -> builder.complianceChannel(Objects.toString(entry.getValue(), ""));
                case "downstream" -> builder.downstreamSubscribersList(extractDownstreamConsumers(entry));
                case "upstream" -> builder.upstreamSubscribersList(extractUpstreamConsumers(entry));
                case "constraints" -> builder.constraintsList(extractConstraintsList(entry));
            }
        }
        return builder.build();
    }

    private static List<Constraints> extractConstraintsList(Map.Entry<Descriptors.FieldDescriptor, Object> entry) {
        SchemataConstraintsBuilder.Constraints constraints = (SchemataConstraintsBuilder.Constraints) entry.getValue();
        List<Constraints> constraintsList = new ArrayList<>();
        for (var constraint : constraints.getConstraintList()) {
            Map<String, Constraints.Constraint> constraintMap = processConstraintConfig(constraint);
            constraintsList.add(new Constraints(constraint.getName(), constraint.getDescription(), constraintMap));
        }
        return constraintsList;
    }

    private static Map<String, Constraints.Constraint> processConstraintConfig(SchemataConstraintsBuilder.Constraint constraint) {
        Map<String, Constraints.Constraint> constraintMap = new HashMap<>();
        for (String key : constraint.getConfigMap().keySet()) {
            var value = constraint.getConfigMap().get(key);
            if (value.hasListValue()) {
                var listValueConstraints = value.getListValue().getValuesList().stream()
                        .map(Value::getStringValue)
                        .collect(Collectors.toList());
                constraintMap.put(key, (Constraints.Constraint.listConstraints(key, listValueConstraints,
                        Constraints.DataType.LIST)));
            } else {
                var dataType = Constraints.DataType.fromString(value.getKindCase().name());
                constraintMap.put(key, (Constraints.Constraint.primitiveConstraints(key, getConstraintValue(value),
                        dataType)));
            }
        }
        return constraintMap;
    }

    private static Object getConstraintValue(Value value) {
        return switch (value.getKindCase()) {
            case NUMBER_VALUE -> value.getNumberValue();
            case STRING_VALUE -> value.getStringValue();
            case BOOL_VALUE -> value.getBoolValue();
            default -> null;
        };
    }


    private static List<Subscribers> extractUpstreamConsumers(Map.Entry<Descriptors.FieldDescriptor, Object> entry) {
        SchemataSubscribersBuilder.Upstream upstream = (SchemataSubscribersBuilder.Upstream) entry.getValue();
        return upstream
                .getSubscribersList()
                .stream()
                .map(subscribe -> new Subscribers(subscribe.getName(), subscribe.getUsage()))
                .collect(Collectors.toList());
    }

    private static List<Subscribers> extractDownstreamConsumers(Map.Entry<Descriptors.FieldDescriptor, Object> entry) {
        SchemataSubscribersBuilder.Downstream downstream = (SchemataSubscribersBuilder.Downstream) entry.getValue();
        return downstream
                .getSubscribersList()
                .stream()
                .map(subscribe -> new Subscribers(subscribe.getName(), subscribe.getUsage()))
                .collect(Collectors.toList());
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
                    case "classification_level" ->
                            builder.classificationLevel(Objects.toString(fieldEntry.getValue(), ""));
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
        return !descriptor.getOptions().getExtension(org.schemata.schema.SchemataBuilder.schemaType)
                .equals(SchemataBuilder.SchemaType.UNKNOWN);
    }

    private boolean isPrimitiveType(Descriptors.FieldDescriptor.Type type, String typeName) {
        return type != Descriptors.FieldDescriptor.Type.MESSAGE || INCLUDED_PRIMITIVE_TYPES.contains(typeName);
    }

}
