package org.schemata.parser;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;

import java.util.List;

public interface Loader {
    public List<Descriptor> loadDescriptors() throws Descriptors.DescriptorValidationException;
}
