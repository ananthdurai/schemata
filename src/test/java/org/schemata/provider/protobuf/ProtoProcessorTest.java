package org.schemata.provider.protobuf;

import com.google.protobuf.Descriptors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.schemata.ResourceLoader;
import org.schemata.domain.Constraints;
import org.schemata.domain.Schema;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ProtoProcessorTest {

    private static Schema userSchema;

    @BeforeAll
    static void setUp()
            throws IOException, Descriptors.DescriptorValidationException {

        var stream = new FileInputStream(ResourceLoader.getDescriptorsPath());
        var protoFileDescriptorLoader = new ProtoFileDescriptorSetLoader(stream);
        var parser = new ProtoProcessor();
        var schemaList = parser.parse(protoFileDescriptorLoader.loadDescriptors());
        assertAll("User Schema Sanity Check", () -> assertNotNull(schemaList), () -> assertEquals(14, schemaList.size()));
        userSchema = schemaList.stream().filter(s -> s.name().equalsIgnoreCase("org.schemata.schema.User")).toList().get(0);
        assertNotNull(userSchema);
    }

    @Test
    @DisplayName("Test User Schema metadata")
    public void checkSchema() {
        assertAll("User Schema properties", () -> assertNotNull(userSchema),
                () -> assertEquals("org.schemata.schema.User", userSchema.name()));
    }

    @Test
    @DisplayName("Test User Fields metadata")
    public void checkFields() {
        assertAll("User Schema Fields Sanity Check", () -> assertNotNull(userSchema.fieldList()),
                () -> assertTrue(userSchema.fieldList().size() > 1));
        var fieldList = userSchema.fieldList();
        assertEquals(6, fieldList.size());
    }

    @Test
    @DisplayName("Test Downstream Subscribers List metadata")
    public void checkDownstreamSubscribersList() {
        assertAll("User Schema Downstream Subscribers Sanity Check", () -> assertNotNull(userSchema.downstreamSubscribersList()),
                () -> assertTrue(userSchema.downstreamSubscribersList().size() > 1));
        var subscribersList = userSchema.downstreamSubscribersList();
        assertEquals(2, subscribersList.size());
    }

    @Test
    @DisplayName("Test Upstream Subscribers List metadata")
    public void checkUpstreamSubscribersList() {
        assertAll("User Schema Upstream Subscribers Sanity Check", () -> assertNotNull(userSchema.upstreamSubscribersList()),
                () -> assertTrue(userSchema.upstreamSubscribersList().size() > 1));
        var subscribersList = userSchema.upstreamSubscribersList();
        assertEquals(2, subscribersList.size());
    }

    @Test
    public void checkConstraintsList() {
        assertAll("User Schema Constraints Sanity Check", () -> assertNotNull(userSchema.constraintsList()),
                () -> assertTrue(userSchema.constraintsList().size() > 1));
        var constraintsList = userSchema.constraintsList();
        assertEquals(2, constraintsList.size());
    }

    @Test
    public void checkPrimitiveConstraints() {
        var constraints = userSchema.constraintsList().stream().filter(v -> v.name().equals("age range")).toList();
        assertEquals(1, constraints.size());
        var constraint = constraints.get(0);
        assertEquals(18.0, constraint.constraintMap().get("min_value").value());
    }

    @Test
    public void checkListConstraints() {
        var constraints = userSchema.constraintsList().stream().filter(v -> v.name().equals("Timezone Constraint")).toList();
        assertEquals(1, constraints.size());
        var constraint = constraints.get(0);
        List<String> expected = List.of("EST", "PST");
        assertEquals(expected, constraint.constraintMap().get("value_set").listValue());
    }
}
