package org.schemata.provider.protobuf;


import com.google.protobuf.DescriptorProtos;
import org.schemata.compatibility.Result;
import org.schemata.compatibility.SchemaCompatibilityChecker;
import org.schemata.compatibility.Summary;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


public class ProtoSchemaCompatibilityChecker implements SchemaCompatibilityChecker {


    static Set<String> EXCLUDED_FILES = Set.of("protobuf/schemata.proto",
            "protobuf/constraints.proto", "protobuf/struct.proto", "protobuf/timestamp.proto",
            "protobuf/descriptor.proto", "protobuf/subscribers.proto", "protobuf/empty.proto");


    static Map<String, String> COMPATIBLE_TYPES = Map.of("TYPE_INT32", "TYPE_INT64", "TYPE_UINT32", "TYPE_UINT64");

    @Override
    public Result check(String baseSchemaPath, String changeSchemaPath) {
        var baseSchemaMap = getSchemaMap(baseSchemaPath);
        var changeSchemaMap = getSchemaMap(changeSchemaPath);
        var summaries = compare(baseSchemaMap, changeSchemaMap);
        return new Result(summaries.size() == 0, summaries);
    }

    private Set<Summary> compare(Map<SchemaKey, SchemaValue> base, Map<SchemaKey, SchemaValue> change) {
        for (var changeEntry : change.entrySet()) {
            if (base.containsKey(changeEntry.getKey())
                    && isDataTypeCompatible(base.get(changeEntry.getKey()), changeEntry.getValue())) {
                base.remove(changeEntry.getKey());
            }
        }
        if (base.size() > 0) {
            return getIncompatibleSchemaChanges(base);
        }
        return Set.of(); // return empty set
    }

    private static Set<Summary> getIncompatibleSchemaChanges(Map<SchemaKey, SchemaValue> base) {
        Set<Summary> summaries = new HashSet<>();
        for (var entry : base.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            summaries.add(new Summary(key.filename, key.messageName(), key.fieldName(), value.type()));
        }
        return summaries;
    }

    private static boolean isDataTypeCompatible(SchemaValue baseValue, SchemaValue changeValue) {
        return baseValue.type.equalsIgnoreCase(changeValue.type) ||
                changeValue.type.equalsIgnoreCase(COMPATIBLE_TYPES.get(baseValue.type.toUpperCase()));
    }

    private Map<SchemaKey, SchemaValue> getSchemaMap(String path) {
        Map<SchemaKey, SchemaValue> schemaValueMap = new HashMap<>();

        try (FileInputStream stream = new FileInputStream(path)) {
            ProtoFileDescriptorSetLoader loader = new ProtoFileDescriptorSetLoader(stream);
            var fileDescriptorProtoMap = loader.indexFileDescriptorProtoByFilename(loader.getDescriptorSet());

            for (var entry : fileDescriptorProtoMap.entrySet()) {
                var descriptor = entry.getValue();
                String filename = getLeafDirectoryAndFileName(descriptor.getName());
                if (EXCLUDED_FILES.contains(filename)) {
                    continue;
                }
                processDescriptorMessages(schemaValueMap, descriptor, filename);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return schemaValueMap;
    }

    private static void processDescriptorMessages(Map<SchemaKey, SchemaValue> schemaValueMap,
                                                  DescriptorProtos.FileDescriptorProto descriptor, String filename) {
        for (var message : descriptor.getMessageTypeList()) {
            for (var field : message.getFieldList()) {
                var key = new SchemaKey(filename, message.getName(), field.getNumber(), field.getName());
                var value = new SchemaValue(field.getType().name());
                schemaValueMap.put(key, value);
            }
        }
    }


    private static String getLeafDirectoryAndFileName(String filePath) {
        File file = new File(filePath);
        String parent = file.getParent();
        String fileName = file.getName();
        if (parent != null && parent.trim().length() > 0) {
            var dirSplit = parent.split("[\\\\/]");
            if (dirSplit.length > 0) {
                return dirSplit[dirSplit.length - 1] + File.separator + fileName;
            }
        }
        return fileName;
    }


    record SchemaKey(String filename, String messageName, int fieldNumber, String fieldName) {
    }

    record SchemaValue(String type) {
    }


}
