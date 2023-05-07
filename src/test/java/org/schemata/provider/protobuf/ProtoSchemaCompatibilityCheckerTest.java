package org.schemata.provider.protobuf;


import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProtoSchemaCompatibilityCheckerTest {


    @Test
    public void testCheck() {
        var checker = new ProtoSchemaCompatibilityChecker();
        var result = checker.check(ResourceLoader.getDescriptorsPath(), ResourceLoader.getChangedDescriptorsPath());
        assertFalse(result.isCompatible());
        assertEquals(2, result.summary().size());
        assertEquals(1, result.summary().stream().filter(summary -> summary.fieldName().equals("name")).toList().size());
    }
}
