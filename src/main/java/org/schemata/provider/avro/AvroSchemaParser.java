package org.schemata.provider.avro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.schemata.domain.EventType;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.domain.Type;
import org.schemata.exception.SchemaParserException;
import org.schemata.provider.SchemaParser;


public class AvroSchemaParser implements SchemaParser {

  static final Map<String, org.apache.avro.Schema.Type> PRIMITIVES = new HashMap<>();

  static {
    PRIMITIVES.put("string", org.apache.avro.Schema.Type.STRING);
    PRIMITIVES.put("bytes", org.apache.avro.Schema.Type.BYTES);
    PRIMITIVES.put("int", org.apache.avro.Schema.Type.INT);
    PRIMITIVES.put("long", org.apache.avro.Schema.Type.LONG);
    PRIMITIVES.put("float", org.apache.avro.Schema.Type.FLOAT);
    PRIMITIVES.put("double", org.apache.avro.Schema.Type.DOUBLE);
    PRIMITIVES.put("boolean", org.apache.avro.Schema.Type.BOOLEAN);
    PRIMITIVES.put("null", org.apache.avro.Schema.Type.NULL);
  }

  @Override
  public List<Schema> getSchemaList(String path) {
    try {
      var schemaFileList = listAvroSchemaFiles(path);
      return schemaFileList.stream().map(s -> {
        try {
          return buildSchema(s);
        } catch (IOException e) {
          throw new SchemaParserException("Error while parsing Avro schema", e);
        }
      }).toList();
    } catch (IOException e) {
      throw new SchemaParserException("Error while parsing Avro schema", e);
    }
  }

  public List<String> listAvroSchemaFiles(String path)
      throws IOException {
    try (Stream<Path> walk = Files.walk(Paths.get(path))) {
      return walk
          .filter(p -> !Files.isDirectory(p))
          .map(p -> p.toString().toLowerCase())
          .filter(f -> f.endsWith("avsc"))
          .toList();
    }
  }

  public Schema buildSchema(String path)
      throws IOException {
    var avroSchema = compileAvroSchema(path);
    List<Field> fields = new ArrayList<>();
    var avroFields = avroSchema.getFields();
    for (org.apache.avro.Schema.Field avroField : avroFields) {
      fields.add(parseField(avroSchema.getFullName(), avroField));
    }
    return parseSchema(avroSchema, fields);
  }

  public org.apache.avro.Schema compileAvroSchema(String path)
      throws IOException {
    return new org.apache.avro.Schema.Parser().parse(new File(path));
  }

  public Schema parseSchema(org.apache.avro.Schema schema, List<Field> fields) {
    Schema.Builder builder = new Schema.Builder(schema.getFullName(), fields);
    builder.description(schema.getProp(Schema.Prop.DESC));
    builder.comment(schema.getProp(Schema.Prop.COMMENT));
    builder.seeAlso(schema.getProp(Schema.Prop.SEE_ALSO));
    builder.reference(schema.getProp(Schema.Prop.REFERENCE));
    builder.owner(schema.getProp(Schema.Prop.OWNER));
    builder.domain(schema.getProp(Schema.Prop.DOMAIN));
    builder.status(schema.getProp(Schema.Prop.STATUS));
    builder.type(handleEmptySchemaType(schema));
    builder.eventType(handleEmptyEventType(schema));
    builder.teamChannel(schema.getProp(Schema.Prop.TEAM_CHANNEL));
    builder.alertChannel(schema.getProp(Schema.Prop.ALERT_CHANNEL));
    builder.complianceOwner(schema.getProp(Schema.Prop.COMPLIANCE_OWNER));
    builder.complianceChannel(schema.getProp(Schema.Prop.COMPLIANCE_CHANNEL));
    return builder.build();
  }

  public Field parseField(String schemaName, org.apache.avro.Schema.Field avroField) {
    String dataType = avroField.schema().getType().getName();
    var builder = new Field.Builder(schemaName, avroField.name(), dataType, isPrimitiveType(dataType));
    builder.description(avroField.getProp(Field.Prop.DESC));
    builder.comment(avroField.getProp(Field.Prop.COMMENT));
    builder.seeAlso(avroField.getProp(Field.Prop.SEE_ALSO));
    builder.reference(avroField.getProp(Field.Prop.REFERENCE));
    builder.isClassified(Boolean.parseBoolean(avroField.getProp(Field.Prop.IS_CLASSIFIED)));
    builder.primaryKey(Boolean.parseBoolean(avroField.getProp(Field.Prop.IS_PRIMARY_KEY)));
    builder.productType(avroField.getProp(Field.Prop.PRODUCT_TYPE));
    return builder.build();
  }

  private String handleEmptyEventType(org.apache.avro.Schema schema) {
    return schema.getProp(Schema.Prop.EVENT_TYPE) == null ? EventType.NONE.name()
        : schema.getProp(Schema.Prop.EVENT_TYPE);
  }

  private String handleEmptySchemaType(org.apache.avro.Schema schema) {
    return schema.getProp(Schema.Prop.SCHEMA_TYPE) == null ? Type.UNKNOWN.name()
        : schema.getProp(Schema.Prop.SCHEMA_TYPE);
  }

  private boolean isPrimitiveType(String name) {
    return PRIMITIVES.containsKey(name);
  }
}
