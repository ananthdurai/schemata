package org.schemata.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.schemata.domain.Field;

import static org.schemata.validate.FieldTrigger.isClassificationLevelEmpty;
import static org.schemata.validate.FieldTrigger.isDescriptionEmpty;


public class FieldValidator implements Function<Field, Result>, Validator<Field> {
  @Override
  public Result apply(Field schema) {

    List<String> errors = new ArrayList<>();
    for (Map.Entry<Rules, FieldTrigger> ruleTrigger : fieldValidatorMap().entrySet()) {
      var result = test(ruleTrigger.getKey(), ruleTrigger.getValue(), schema);
      result.ifPresent(errors::add);
    }
    return errors.size() == 0 ? new Result(Status.SUCCESS, errors) : new Result(Status.ERROR, errors);
  }

  private Map<Rules, FieldTrigger> fieldValidatorMap() {
    return Map.of(Rules.FIELD_DESCRIPTION_EMPTY, isDescriptionEmpty, Rules.FIELD_CLASSIFICATION_EMPTY,
        isClassificationLevelEmpty);
  }
}
