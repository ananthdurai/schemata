package org.schemata;

import com.google.protobuf.Descriptors;
import org.schemata.app.DocumentApp;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.domain.Schema;
import org.schemata.parser.Loader;
import org.schemata.parser.ProtoFileDescriptorSetLoader;
import org.schemata.parser.SchemaParser;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;


@Command(name = "protocol", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor {

  private List<Schema> schemaList;

  public SchemataExecutor() {
  }

  @Option(names = {"-p", "--descriptor-path"}, description = "Path to descriptor file", scope = ScopeType.INHERIT)
  private File path;

  @Command(description = "Validate schema")
  public int validate()
      throws Exception {
    System.out.println("entered in validate");
    loadSchema();
    return new SchemaValidatorApp(schemaList).call();
  }

  @Command(description = "Calculate protocol score")
  public int score(@Parameters(paramLabel = "<schema>", description = "fully qualified message name") String schema)
      throws Exception {
    loadSchema();
    return new SchemaScoreApp(schemaList, schema).call();
  }

  @Command(description = "Document a schema as JSON")
  public int document()
      throws Exception {
    loadSchema();
    return new DocumentApp(schemaList).call();
  }

  private void loadSchema()
      throws IOException, Descriptors.DescriptorValidationException {
    Loader loader;

    if (path == null) {
      throw new IllegalArgumentException("Path can't be null");
    } else {
      var stream = new FileInputStream(path);
      loader = new ProtoFileDescriptorSetLoader(stream);
    }

    var descriptors = loader.loadDescriptors();
    schemaList = new SchemaParser().parse(descriptors);
  }
}
