package org.schemata;

import picocli.CommandLine;


public class SchemataMain {

  public static void main(String... args) {
    int exitCode = new CommandLine(new SchemataExecutor()).execute(args);
    System.exit(exitCode);
  }
}
