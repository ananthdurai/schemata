package org.schemata.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.schemata.domain.Schema;

import java.util.List;
import java.util.concurrent.Callable;

public class DocumentApp implements Callable<Integer> {
    private final List<Schema> schemaList;

    public DocumentApp(List<Schema> schemaList) {
        this.schemaList = schemaList;
    }

    @Override
    public Integer call() throws Exception {
        var mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print

        mapper.writeValue(System.out, schemaList);

        return 0;
    }
}
