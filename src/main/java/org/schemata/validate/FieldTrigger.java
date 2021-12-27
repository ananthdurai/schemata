package org.schemata.validate;

import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.schemata.domain.Field;


public interface FieldTrigger extends Predicate<Field> {

  FieldTrigger isDescriptionEmpty = field -> StringUtils.isBlank(field.description());
  FieldTrigger isClassificationLevelEmpty =
      field -> field.isClassified() && StringUtils.isBlank(field.classificationLevel());
}
