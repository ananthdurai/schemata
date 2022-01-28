package org.schemata.parser;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

import java.io.FileDescriptor;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.util.SupplierUtil;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.schema.SchemataBuilder;


public class SchemaParser {

  private static final Set<String> INCLUDED_PRIMITIVE_TYPES = Set.of("google.protobuf.Timestamp");

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

  public List<Schema> parseSchema(DescriptorProtos.FileDescriptorSet descriptorSet) {
    // key files protos by name for easier lookup
    var filesByName = descriptorSet
            .getFileList()
            .stream()
            .collect(Collectors.toMap(DescriptorProtos.FileDescriptorProto::getName, file -> file));

    // build new DAG of file dependencies
    var dependencyFilenames = new DirectedAcyclicGraph<String, String>(
            SupplierUtil.createSupplier(String.class),
            SupplierUtil.createSupplier(String.class), false);

    // populate the graph
    descriptorSet
            .getFileList()
            .stream()
            .forEach(file -> {
              dependencyFilenames.addVertex(file.getName());

              file.getDependencyList().stream().forEach(dependency -> {
                // adding a vertex is idempotent
                dependencyFilenames.addVertex(dependency);

                // TODO: which direction should this be added to get the correct topological iterator?
                dependencyFilenames.addEdge(dependency, file.getName());
              });
            });

    HashMap<String, Descriptors.FileDescriptor> descriptors = new HashMap<>();
    dependencyFilenames.forEach(filename -> {
      var file = filesByName.get(filename);
      var dependenciesForFile = file
              .getDependencyList()
              .stream()
              .map(descriptors::get).toArray(size -> new Descriptors.FileDescriptor[size]);

      try {
        var descriptor = Descriptors.FileDescriptor.buildFrom(file, dependenciesForFile);
        descriptors.put(filename, descriptor);
      } catch (Descriptors.DescriptorValidationException e) {
        e.printStackTrace();
      }
    });

    descriptors.forEach((filename, fileDescriptor) -> {

    });

    // build each file into a FileDescriptor
//    var allMessages = descriptorSet
//            .getFileList()
//            .stream()
//            .map(file -> {
//              var dependencies = file.getDependencyList()
//                      .stream()
//                      .map(fileName -> filesByName.get(fileName));
//              return Descriptors.FileDescriptor.buildFrom(file,dependencies);
//            });
//
//    for (var descriptor : allMessages.toList()) {
////      descriptor.getFields)_;
//    }


    List<Schema> schemaList = new ArrayList<>();
//    for (var descriptorType : descriptors) {
//      String schemaName = descriptorType.getFullName();
//      // Extract all the metadata for the fieldList
//      var fieldList = extractFields(descriptorType.getFields(), schemaName);
//      var schema = this.extractSchema(descriptorType, descriptorType.getFullName(), fieldList);
//      schemaList.add(schema);
//    }
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
        case "event_type" -> builder.eventType(entry.getValue().toString());
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

  private boolean isPrimitiveType(Descriptors.FieldDescriptor.Type type, String typeName) {
    return type != Descriptors.FieldDescriptor.Type.MESSAGE || INCLUDED_PRIMITIVE_TYPES.contains(typeName);
  }
}
