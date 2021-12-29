package org.schemata;

import java.util.List;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import picocli.CommandLine;


public class SchemataMain {

  public static void main(String... args) {
    List<Schema> schemaList = new SchemaParser().parseSchema(SchemaRegistry.registerSchema());
    int exitCode = new CommandLine(new SchemataExecutor(schemaList)).execute(args);
    System.exit(exitCode);
  }
}
