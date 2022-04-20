package org.schemata;

import org.schemata.app.DocumentApp;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.domain.Schema;
import org.schemata.provider.protobuf.ProtoSchemaParser;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;


@Command(name = "protocol", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor {

  private List<Schema> schemaList;

  public SchemataExecutor() {
  }

  @Option(names = {"-p", "--descriptor-path"}, description = "Path to descriptor file", scope = ScopeType.INHERIT)
  private String path;

  @Command(description = "Validate schema")
  public int validate()
      throws Exception {
    var parser = new ProtoSchemaParser();
    return new SchemaValidatorApp(parser.getSchemaList(path)).call();
  }

  @Command(description = "Calculate protocol score")
  public int score(@Parameters(paramLabel = "<schema>", description = "fully qualified message name") String schema)
      throws Exception {
    var parser = new ProtoSchemaParser();
    return new SchemaScoreApp(parser.getSchemaList(path), schema).call();
  }

  @Command(description = "Document a schema as JSON")
  public int document()
      throws Exception {
    var parser = new ProtoSchemaParser();
    return new DocumentApp(parser.getSchemaList(path)).call();
  }
}
