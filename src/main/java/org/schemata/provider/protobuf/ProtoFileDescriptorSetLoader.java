package org.schemata.provider.protobuf;

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

/**
 * Loads message descriptors from <code>DescriptorProtos.FileDescriptorSet</code> provided directly or by InputStream
 */
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
    public List<Descriptors.Descriptor> loadDescriptors() throws Descriptors.DescriptorValidationException {// we need to build a DAG of filenames so that we can build the Descriptors.Descriptor objects
        // we need to build a DAG of filenames that import each other, so that we can build the Descriptors.Descriptor
        // objects in the the correct order, providing each one with a Descriptor for each file it imports
        var dependencyFilenames = buildFileDependencyGraph(descriptorSet);

        // we key the basic proto representations of the FileDescriptor by filename for simpler retrieval
        var fileDescriptorProtosByName = indexFileDescriptorProtosByFilename(descriptorSet);

        // these be the parsed FileDescriptor objects (again keyed by filename) so that they can be passed back into
        // the instantiation of any other files that import them
        var descriptors = new HashMap<String, Descriptors.FileDescriptor>();

        // forEach for the DAG is executed in topological sort order so that we first create the
        // FileDescriptors for the leaves in the dependency graph, working backwards to the roots
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

        // lastly, we collect out each of the message Descriptor objects from the FileDescriptors into a flat list
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
                // dependencies point to the file that imports them rather than the other way round
                // to ensure we can traverse the graph in the correct order (depth first)
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