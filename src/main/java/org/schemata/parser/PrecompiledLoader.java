package org.schemata.parser;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

import java.util.List;

public class PrecompiledLoader implements Loader {

    List<GeneratedMessageV3> messages;

    public PrecompiledLoader(List<GeneratedMessageV3> messages) {
        this.messages = messages;
    }

    @Override
    public List<Descriptors.Descriptor> loadDescriptors() {
        return messages
                .stream()
                .map(GeneratedMessageV3::getDescriptorForType)
                .toList();
    }
}
