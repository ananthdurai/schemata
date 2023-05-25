package org.schemata.provider.dbt;

import org.schemata.compatibility.Result;
import org.schemata.compatibility.SchemaCompatibilityChecker;
import org.schemata.compatibility.Summary;
import org.schemata.domain.Schema;


import java.util.*;

/**
 * Compare the base schema with the change schema and return the incompatible changes.
 */
public class DbtSchemaCompatibilityChecker implements SchemaCompatibilityChecker {
    @Override
    public Result check(String baseSchemaPath, String changeSchemaPath) {
        var baseSchema = new DbtSchemaParser().getSchemaList(baseSchemaPath);
        var changeSchema = new DbtSchemaParser().getSchemaList(changeSchemaPath);
        var summaries = compare(buildSchemaMap(baseSchema), buildSchemaMap(changeSchema));
        return new Result(summaries.size() == 0, summaries);
    }

    private Map<SchemaKey, SchemaValue> buildSchemaMap(List<Schema> schemaList) {
        Map<SchemaKey, SchemaValue> schemaMap = new HashMap<>();
        for (var schema : schemaList) {
            for (var field : schema.fieldList()) {
                schemaMap.put(new SchemaKey(schema.name(), field.name()), new SchemaValue(field.dataType()));
            }
        }
        return schemaMap;
    }

    /**
     * The current data type validation is a 'strict type' validation. It doesn't support `type boxing` support.
     *
     * @param base   base schema value
     * @param change change schema value
     * @return Summary of Incompatible changes
     */
    private Set<Summary> compare(Map<SchemaKey, SchemaValue> base, Map<SchemaKey, SchemaValue> change) {

        for (var entry : change.entrySet()) {
            if (base.containsKey(entry.getKey())
                    && isDataTypeCompatible(base.get(entry.getKey()), entry.getValue())) {
                base.remove(entry.getKey());
            }
        }
        if (base.size() > 0) {
            return getIncompatibleSchemaChanges(base);
        }
        return Set.of(); // return empty set
    }

    /**
     * The current data type validation is a 'strict type' validation. It doesn't support `type boxing` support.
     * We intend to enrich this compatibility check in the future. For example, `int32` and `int64` are compatible.
     *
     * @param baseValue   base schema value
     * @param changeValue change schema value
     * @return true if compatible, false otherwise
     */
    private boolean isDataTypeCompatible(SchemaValue baseValue, SchemaValue changeValue) {
        return baseValue.type.equalsIgnoreCase(changeValue.type);
    }

    /**
     * Loop through the base schema map and build a set of incompatible schema changes.
     *
     * @param base base schema map
     * @return set of incompatible schema changes
     */
    private static Set<Summary> getIncompatibleSchemaChanges(Map<SchemaKey, SchemaValue> base) {
        Set<Summary> summaries = new HashSet<>();
        for (var entry : base.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            summaries.add(new Summary.Builder().fieldName(key.fieldName)
                    .schemaName(key.table)
                    .fieldType(value.type).build());
        }
        return summaries;
    }


    record SchemaKey(String table, String fieldName) {
    }

    record SchemaValue(String type) {
    }
}
