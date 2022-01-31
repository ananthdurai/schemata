package org.schemata;

import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import org.schemata.printer.Console;

import java.util.List;
import java.util.Set;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;


@Command(name = "schemata", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor {

  private final List<Schema> schemaList;
  private final SchemaParser parser;

  public SchemataExecutor(SchemaParser parser) {
    this.parser = parser;
    schemaList = this.parser.parseSchema(SchemaRegistry.registerSchema());
  }

  @Command()
  public int validate() throws Exception {
    return new SchemaValidatorApp(schemaList).call();
  }

  @Command()
  public int score(@Parameters String schema)
          throws Exception {
    return new SchemaScoreApp(schemaList, schema).call();
  }
}


//  @Override
//  public Integer call()
//      throws Exception {
//
//    if (StringUtils.isBlank(cmdType) || !validCommands.contains(cmdType.toLowerCase())) {
//      Console.printError("Given command:" + cmdType + " is invalid");
//      return -1;
//    }
//
//    if (schemaList == null || schemaList.size() == 0) {
//      Console.printError("Invalid Schema list:" + schemaList);
//      return -1;
//    }
//
//    int code = switch (cmdType.toLowerCase()) {
//      case VALIDATE_COMMAND -> new SchemaValidatorApp(schemaList).call();
//      case SCORE_COMMAND -> callScore();
//      default -> -1;
//    };
//    return code;
//  }


