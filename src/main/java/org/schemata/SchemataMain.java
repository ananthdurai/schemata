package org.schemata;

import org.schemata.parser.SchemaParser;
import picocli.CommandLine;


public class SchemataMain {

  public static void main(String... args) {
    var parser = new SchemaParser();
    int exitCode = new CommandLine(new SchemataExecutor()).execute(args);
    System.exit(exitCode);
  }
}
