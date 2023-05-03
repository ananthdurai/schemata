package org.schemata.domain;

import java.util.List;
import java.util.Map;

public record Constraints(String name, String description, Map<String, Constraint> constraintMap) {

    public record Constraint(String key, Object value, DataType dataType, List<String> listValue) {

        public static Constraint primitiveConstraints(String key, Object value, DataType dataType) {
            return new Constraint(key, value, dataType, null);
        }

        public static Constraint listConstraints(String key, List<String> listValue, DataType dataType) {
            return new Constraint(key, null, dataType, listValue);
        }

    }

    public enum DataType {
        NULL, NUMBER, STRING, BOOLEAN, LIST; // STRUCT Type is not supported

        public static DataType fromString(String dataType) {
            return switch (dataType.toUpperCase()) {
                case "NULL_VALUE", "NULL" -> NULL;
                case "NUMBER_VALUE", "NUMBER" -> NUMBER;
                case "STRING_VALUE", "STRING" -> STRING;
                case "BOOL_VALUE", "BOOLEAN" -> BOOLEAN;
                case "LIST_VALUE", "LIST" -> LIST;
                default -> throw new IllegalArgumentException("Invalid data type: " + dataType);
            };
        }
    }
}
