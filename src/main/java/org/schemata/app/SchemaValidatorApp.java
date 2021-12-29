package org.schemata.app;

import java.util.List;
import java.util.concurrent.Callable;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.printer.Console;
import org.schemata.validate.FieldValidator;
import org.schemata.validate.SchemaValidator;
import org.schemata.validate.Status;


public class SchemaValidatorApp implements Callable<Integer> {

  private List<Schema> schemaList;

  public SchemaValidatorApp(List<Schema> schemaList) {
    this.schemaList = schemaList;
  }

  @Override
  public Integer call()
      throws Exception {
    var schemaValidator = new SchemaValidator();
    var fieldValidator = new FieldValidator();
    for (Schema schema : schemaList) {
      var schemaResult = schemaValidator.apply(schema);
      if (schemaResult.status() == Status.ERROR) {
        Console.printError("Error parsing Schema " + schema.name() + "Error Message:" + schemaResult.errorMessages());
        return -1;
      }

      for (Field field : schema.fieldList()) {
        var fieldResult = fieldValidator.apply(field);
        if (fieldResult.status() == Status.ERROR) {
          Console.printError(
              "Error parsing Schema Fields in schema:" + schema.name() + " on field:" + field.name() + " Error Message:"
                  + fieldResult.errorMessages());
          return -1;
        }
      }
    }
    Console.printSuccess("Schema validation success. No error to report");
    return 0;
  }
}
