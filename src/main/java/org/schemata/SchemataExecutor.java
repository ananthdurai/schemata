package org.schemata;

import org.schemata.app.DocumentApp;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.provider.SchemaParser;
import org.schemata.provider.avro.AvroSchemaParser;
import org.schemata.provider.protobuf.ProtoSchemaParser;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;


@Command(name = "protocol", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor {

  enum Provider {
    PROTOBUF, AVRO
  }

  @Option(names = {"-s", "--source"}, description = "Path to schema file", scope = ScopeType.INHERIT)
  private String path;

  @Option(names = {"-p", "--provider"}, description = "Valid provider values: ${COMPLETION-CANDIDATES}", scope =
      ScopeType.INHERIT)
  private Provider provider;

  @Command(description = "Validate schema")
  public int validate()
      throws Exception {
    var parser = getSchemaParser();
    return new SchemaValidatorApp(parser.getSchemaList(path)).call();
  }

  @Command(description = "Calculate protocol score")
  public int score(@Parameters(paramLabel = "<schema>", description = "fully qualified message name") String schema)
      throws Exception {
    var parser = getSchemaParser();
    return new SchemaScoreApp(parser.getSchemaList(path), schema).call();
  }

  @Command(description = "Document a schema as JSON")
  public int document()
      throws Exception {
    var parser = getSchemaParser();
    return new DocumentApp(parser.getSchemaList(path)).call();
  }

  public SchemaParser getSchemaParser() {
    return switch (provider) {
      case PROTOBUF -> new ProtoSchemaParser();
      case AVRO -> new AvroSchemaParser();
    };
  }
}
