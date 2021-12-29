package org.schemata;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.schemata.app.SchemaScoreApp;
import org.schemata.app.SchemaValidatorApp;
import org.schemata.domain.Schema;
import org.schemata.printer.Console;
import picocli.CommandLine;


@CommandLine.Command(name = "schemata", mixinStandardHelpOptions = true, description = "Schemata commandline tool")
public class SchemataExecutor implements Callable<Integer> {

  private List<Schema> schemaList;
  private static final String VALIDATE_COMMAND = "validate";
  private static final String SCORE_COMMAND = "score";
  private static final Set<String> validCommands = Set.of(VALIDATE_COMMAND, SCORE_COMMAND);

  @CommandLine.Option(names = "--cmd", description = "Type of the command to execute.", defaultValue = "validate", required = true)
  String cmdType;

  @CommandLine.Parameters()
  private List<String> positionalParams;

  public SchemataExecutor(List<Schema> schemaList) {
    this.schemaList = schemaList;
  }

  @Override
  public Integer call()
      throws Exception {

    if (StringUtils.isBlank(cmdType) || !validCommands.contains(cmdType.toLowerCase())) {
      Console.printError("Given command:" + cmdType + " is invalid");
      return -1;
    }

    if (schemaList == null || schemaList.size() == 0) {
      Console.printError("Invalid Schema list:" + schemaList);
      return -1;
    }

    int code = switch (cmdType.toLowerCase()) {
      case VALIDATE_COMMAND -> new SchemaValidatorApp(schemaList).call();
      case SCORE_COMMAND -> callScore();
      default -> -1;
    };
    return code;
  }

  private Integer callScore()
      throws Exception {
    if (positionalParams == null || positionalParams.size() < 2) {
      Console.printError("Schema name not found in the positional argument:" + positionalParams);
      return -1;
    }
    String schema = positionalParams.get(1);
    return new SchemaScoreApp(schemaList, schema).call();
  }
}
