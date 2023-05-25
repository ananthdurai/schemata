package org.schemata.provider.dbt;

import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;

import static org.junit.jupiter.api.Assertions.*;

public class DbtSchemaCompatibilityCheckerTest {

    @Test
    public void testValidSchemaChangesCheck() {
        var checker = new DbtSchemaCompatibilityChecker();
        var result = checker.check(ResourceLoader.getDbtBasePath(), ResourceLoader.getDbtBasePath());
        assertTrue(result.isCompatible());
    }

    @Test
    public void testInValidSchemaChangesCheck() {
        var checker = new DbtSchemaCompatibilityChecker();
        var result = checker.check(ResourceLoader.getDbtBasePath(), ResourceLoader.getChangedDbtBasePath());
        assertFalse(result.isCompatible());
    }
}
