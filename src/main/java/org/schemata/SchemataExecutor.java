package org.schemata;

import org.schemata.app.DocumentApp;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.compatibility.SchemaCompatibilityChecker;
import org.schemata.compatibility.Summary;
import org.schemata.provider.SchemaParser;
import org.schemata.provider.avro.AvroSchemaCompatibilityChecker;
import org.schemata.provider.avro.AvroSchemaParser;
import org.schemata.provider.protobuf.ProtoSchemaCompatibilityChecker;
import org.schemata.provider.protobuf.ProtoSchemaParser;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import java.util.Set;

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

  @Option(names = {"-b", "--base"}, description = "Base Path to schema file", scope = ScopeType.INHERIT)
  private String basePath;

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

  @Command(description = "Check if schema is backward compatible")
  public boolean isBackwardCompatible() {
    var checker = getSchemaCompatibilityChecker();
    return checker.check(basePath, path).isCompatible();
  }

  @Command(description = "Print the backward compatibility summary with incompatible fields")
  public Set<Summary> compatibilitySummary() {
    var checker = getSchemaCompatibilityChecker();
    return checker.check(basePath, path).summary();
  }

  public SchemaParser getSchemaParser() {
    return switch (provider) {
      case PROTOBUF -> new ProtoSchemaParser();
      case AVRO -> new AvroSchemaParser();
    };
  }

  public SchemaCompatibilityChecker getSchemaCompatibilityChecker() {
    return switch (provider) {
      case PROTOBUF -> new ProtoSchemaCompatibilityChecker();
      case AVRO -> new AvroSchemaCompatibilityChecker();
    };
  }
}
