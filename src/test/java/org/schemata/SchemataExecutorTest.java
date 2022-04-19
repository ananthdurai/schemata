package org.schemata;

import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schemata.parser.proto.SchemaParser;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SchemataExecutorTest {

  static CommandLine cmd;

  @BeforeAll
  static void setup() {
    var parser = new SchemaParser();
    var executor = new SchemataExecutor();
    cmd = new CommandLine(executor);
  }

  @Test
  @Description("Run schema validate function to run all the schema and fields validation rules")
  public void testSchemaValidateCmd() {
    int exitCode = cmd.execute("validate", "-p=" + ResourceLoader.getDescriptorsPath());
    assertEquals(0, exitCode);
  }

  @Test
  @Description("Test Schema score with an invalid schema name")
  public void testScoreWithInvalidSchema() {
    int exitCode = cmd.execute("score", "-p=" + ResourceLoader.getDescriptorsPath(), "User");
    assertEquals(-1, exitCode);
  }

  @Test
  @Description("Test Schema score with an valid schema name")
  public void testScoreWithValidSchema() {
    int exitCode = cmd.execute("score", "-p=" + ResourceLoader.getDescriptorsPath(),
        "org.schemata.schema.CampaignCategoryTrackerEvent");
    assertEquals(0, exitCode);
  }

  @Test
  @Description("Test Schema documentation")
  public void testSchemaDocumentationCmd() {
    int exitCode = cmd.execute("document", "-p=" + ResourceLoader.getDescriptorsPath());
    assertEquals(0, exitCode);
  }
}
