package org.schemata.parser;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.util.SupplierUtil;
import org.schemata.schema.SchemataBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProtoFileDescriptorSetLoader implements Loader {

    private final DescriptorProtos.FileDescriptorSet descriptorSet;

    public ProtoFileDescriptorSetLoader(DescriptorProtos.FileDescriptorSet descriptorSet) {
        this.descriptorSet = descriptorSet;
    }

    public ProtoFileDescriptorSetLoader(InputStream stream) throws IOException {
        var registry = ExtensionRegistry.newInstance();
        SchemataBuilder.registerAllExtensions(registry);

        this.descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(stream, registry);
    }

    @Override
    public List<Descriptors.Descriptor> loadDescriptors() throws Descriptors.DescriptorValidationException {
        var dependencyFilenames = buildFileDependencyGraph(descriptorSet);
        var fileDescriptorProtosByName = indexFileDescriptorProtosByFilename(descriptorSet);

        var descriptors = new HashMap<String, Descriptors.FileDescriptor>();
        // forEach for DAG is executed in topological sort order
        for (String filename : dependencyFilenames) {
            var file = fileDescriptorProtosByName.get(filename);
            var dependenciesForFile = file
                    .getDependencyList()
                    .stream()
                    .map(descriptors::get)
                    .toArray(Descriptors.FileDescriptor[]::new);

            var descriptor = Descriptors.FileDescriptor.buildFrom(file, dependenciesForFile);
            descriptors.put(filename, descriptor);
        }

        return collectAllMessageDescriptors(descriptors.values());
    }

    private DirectedAcyclicGraph<String, String> buildFileDependencyGraph(DescriptorProtos.FileDescriptorSet descriptorSet) {
        var dependencyFilenames = new DirectedAcyclicGraph<String, String>(
                SupplierUtil.createSupplier(String.class),
                SupplierUtil.createSupplier(String.class), false);

        // populate the graph
        for (var fileDescriptorProto : descriptorSet.getFileList()) {
            dependencyFilenames.addVertex(fileDescriptorProto.getName());
            for (String dependency : fileDescriptorProto.getDependencyList()) {// adding a vertex is idempotent
                dependencyFilenames.addVertex(dependency);
                dependencyFilenames.addEdge(dependency, fileDescriptorProto.getName());
            }
        }
        return dependencyFilenames;
    }

    private Map<String, DescriptorProtos.FileDescriptorProto> indexFileDescriptorProtosByFilename
            (DescriptorProtos.FileDescriptorSet descriptorSet) {
        return descriptorSet
                .getFileList()
                .stream()
                .collect(Collectors.toMap(DescriptorProtos.FileDescriptorProto::getName, file -> file));
    }

    private List<Descriptors.Descriptor> collectAllMessageDescriptors
            (Collection<Descriptors.FileDescriptor> descriptors) {
        return descriptors
                .stream()
                .flatMap(fileDescriptor -> fileDescriptor.getMessageTypes().stream())
                .toList();
    }

}