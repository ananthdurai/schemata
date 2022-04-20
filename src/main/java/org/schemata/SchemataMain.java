package org.schemata;

import picocli.CommandLine;


public class SchemataMain {

  public static void main(String... args) {
    var cmd = new CommandLine(new SchemataExecutor())
        .setOptionsCaseInsensitive(true)
        .setCaseInsensitiveEnumValuesAllowed(true);

    int exitCode = cmd.execute(args);
    System.exit(exitCode);
  }
}
