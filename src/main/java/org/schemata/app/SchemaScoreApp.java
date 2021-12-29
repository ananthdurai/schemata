package org.schemata.app;

import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaNotFoundException;
import org.schemata.graph.SchemaGraph;
import org.schemata.printer.Console;


public class SchemaScoreApp implements Callable<Integer> {

  private List<Schema> schemaList;
  private String schemaName;

  public SchemaScoreApp(List<Schema> schemaList, String schemaName) {
    this.schemaList = schemaList;
    this.schemaName = schemaName;
  }

  @Override
  public Integer call()
      throws Exception {

    if (StringUtils.isBlank(schemaName)) {
      Console.printError("Invalid schema name:" + schemaName);
      return -1;
    }

    var graph = new SchemaGraph(this.schemaList);
    try {
      double value = graph.getSchemataScore(schemaName);
      Console.printSuccess("Schemata score for " + schemaName + " : " + value);
    } catch (SchemaNotFoundException e) {
      Console.printError(e.getMessage());
      return -1;
    }
    return 0;
  }
}
