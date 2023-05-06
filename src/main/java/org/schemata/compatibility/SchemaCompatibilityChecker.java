package org.schemata.compatibility;


public interface SchemaCompatibilityChecker {
    Result check(String baseSchemaPath, String changeSchemaPath);
}

