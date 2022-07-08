package org.schemata;

import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SchemataExecutorTest {

  static CommandLine cmd;

  @BeforeAll
  static void setup() {
    var executor = new SchemataExecutor();
    cmd = new CommandLine(executor);
  }

  @Test
  @Description("Run schema validate function to run all the schema and column validation rules")
  public void testSchemaValidateCmd() {
    int exitCode = cmd.execute("validate", "-s=" + ResourceLoader.getDescriptorsPath(), "-p=PROTOBUF");
    assertEquals(0, exitCode);
  }

  @Test
  @Description("Test Schema score with an invalid schema name")
  public void testScoreWithInvalidSchema() {
    int exitCode = cmd.execute("score", "-s=" + ResourceLoader.getDescriptorsPath(), "User", "-p=PROTOBUF");
    assertEquals(-1, exitCode);
  }

  @Test
  @Description("Test Schema score with an valid schema name")
  public void testScoreWithValidSchema() {
    int exitCode = cmd.execute("score", "-s=" + ResourceLoader.getDescriptorsPath(), "-p=PROTOBUF",
        "org.schemata.schema.CampaignCategoryTrackerEvent");
    assertEquals(0, exitCode);
  }

  @Test
  @Description("Test Schema documentation")
  public void testSchemaDocumentationCmd() {
    int exitCode = cmd.execute("document", "-s=" + ResourceLoader.getDescriptorsPath(), "-p=PROTOBUF");
    assertEquals(0, exitCode);
  }
}
