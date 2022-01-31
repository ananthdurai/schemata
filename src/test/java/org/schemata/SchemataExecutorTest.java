package org.schemata;


import java.util.List;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SchemataExecutorTest {

  static CommandLine cmd;

  @BeforeAll
  static void setup() {
    var parser = new SchemaParser();
    var executor = new SchemataExecutor(parser);
    cmd = new CommandLine(executor);
  }

  @Test
  @Description("Run schema validate function to run all the schema and fields validation rules")
  public void testSchemaValidateCmd() {
    int exitCode = cmd.execute("validate");
    assertEquals(0, exitCode);
  }

  @Test
  @Description("Test Schema score with an invalid schema name")
  public void testScoreWithInvalidSchema() {
    int exitCode = cmd.execute("score", "User");
    assertEquals(-1, exitCode);
  }

  @Test
  @Description("Test Schema score with an valid schema name")
  public void testScoreWithValidSchema() {
    int exitCode = cmd.execute("score", "org.schemata.schema.CampaignCategoryTrackerEvent");
    assertEquals(0, exitCode);
  }
}
