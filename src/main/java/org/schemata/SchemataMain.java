package org.schemata;

import java.util.List;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import picocli.CommandLine;


public class SchemataMain {

  public static void main(String... args) {
    var parser = new SchemaParser();
    int exitCode = new CommandLine(new SchemataExecutor(parser)).execute(args);
    System.exit(exitCode);
  }
}
