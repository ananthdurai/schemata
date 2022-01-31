package org.schemata;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.StringUtils;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import org.schemata.printer.Console;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;


@Command(name = "schemata", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor {

  private List<Schema> schemaList;
  private final SchemaParser parser;

  public SchemataExecutor(SchemaParser parser) {
    this.parser = parser;
  }

  @Option(names = {"-p", "--descriptor-path"}, description = "Path to descriptor file", scope = ScopeType.INHERIT)
  private File path;

  @Command(description = "Validate schema")
  public int validate() throws Exception {
    loadSchema();
    return new SchemaValidatorApp(schemaList).call();
  }

  @Command(description = "Calculate schemata score")
  public int score(@Parameters(paramLabel = "<schema>", description = "fully qualified message name") String schema)
          throws Exception {
    loadSchema();
    return new SchemaScoreApp(schemaList, schema).call();
  }

  private void loadSchema() throws IOException, Descriptors.DescriptorValidationException {
    if (path == null) {
      schemaList = parser.parseSchema(SchemaRegistry.registerSchema());
    } else {
      var stream = new FileInputStream(path);
      var descriptorSet = parser.loadDescriptorSet(stream);
      schemaList = parser.parseSchema(descriptorSet);
    }
  }
}
