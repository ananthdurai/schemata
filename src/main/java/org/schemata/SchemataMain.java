package org.schemata;

import com.google.protobuf.GeneratedMessageV3;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.parser.SchemaParser;
import org.schemata.schema.SchemataBuilder;
import org.schemata.validate.FieldValidator;
import org.schemata.validate.SchemaValidator;
import org.schemata.validate.Status;


public class SchemataMain {

  private static final String VALIDATE_COMMAND = "validate";
  private static final String SCORE_COMMAND = "score";
  private static final Set<String> validCommands = new HashSet<>(Arrays.asList(VALIDATE_COMMAND, SCORE_COMMAND));

  public static void main(String... args) {

    if (args.length < 1) {
      throw new IllegalArgumentException("SchemaGuard expect either validate or score as an argument");
    }

    if (!validCommands.contains(args[0].trim().toLowerCase())) {
      throw new IllegalArgumentException("SchemaGuard expect either validate or score as an argument");
    }

    String command = args[0];

    if (command.equalsIgnoreCase(SCORE_COMMAND) && args.length < 2) {
      throw new IllegalArgumentException("Entity name is missing. Score command requires entity name to score");
    }

    List<Schema> schemaList = new SchemaParser().parseSchema(registerSchema());

    if (command.equalsIgnoreCase(VALIDATE_COMMAND)) {
      var schemaValidator = new SchemaValidator();
      var fieldValidator = new FieldValidator();
      for (Schema schema : schemaList) {
        var schemaResult = schemaValidator.apply(schema);
        if (schemaResult.status() == Status.ERROR) {
          System.out.println("Error parsing Schema " + schema.name() + "Error Message:" + schemaResult.errorMessages());
          return;
        }

        for (Field field : schema.fieldList()) {
          var fieldResult = fieldValidator.apply(field);
          if (fieldResult.status() == Status.ERROR) {
            System.out.println("Error parsing Schema Fields in schema:" + schema.name() + " on field:" + field.name()
                + " Error Message:" + fieldResult.errorMessages());
            return;
          }
        }
      }
      System.out.println("Schema validation success. No error to report");
      return;
    }

    if (command.equalsIgnoreCase(SCORE_COMMAND)) {

    }
  }

  private static List<GeneratedMessageV3> registerSchema() {
    return List.of(SchemataBuilder.Person.newBuilder().build());
  }
}
