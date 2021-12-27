package org.schemata.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.schemata.domain.Schema;

import static org.schemata.validate.SchemaTrigger.*;


public class SchemaValidator implements Function<Schema, Result>, Validator<Schema> {

  @Override
  public Result apply(Schema schema) {

    List<String> errors = new ArrayList<>();
    for (Map.Entry<Rules, SchemaTrigger> ruleTrigger : schemaValidatorMap().entrySet()) {
      var result = test(ruleTrigger.getKey(), ruleTrigger.getValue(), schema);
      result.ifPresent(errors::add);
    }

    return errors.size() == 0 ? new Result(Status.SUCCESS, errors) : new Result(Status.ERROR, errors);
  }

  private Map<Rules, SchemaTrigger> schemaValidatorMap() {
    return Map.of(Rules.SCHEMA_DESCRIPTION_EMPTY, isDescriptionEmpty, Rules.SCHEMA_OWNER_EMPTY, isOwnerEmpty,
        Rules.SCHEMA_DOMAIN_EMPTY, isDomainEmpty, Rules.SCHEMA_UNKNOWN_TYPE, isInValidType,
        Rules.SCHEMA_VALID_ENTITY_WITH_PRIMARY_KEY, isPrimaryKeyNotExistsForEntity);
  }
}
