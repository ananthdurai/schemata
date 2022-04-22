package org.schemata.validate;

import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.domain.Type;


public interface SchemaTrigger extends Predicate<Schema> {

  SchemaTrigger isDescriptionEmpty = schema -> StringUtils.isBlank(schema.description());

  SchemaTrigger isOwnerEmpty = schema -> StringUtils.isBlank(schema.owner());

  SchemaTrigger isDomainEmpty = schema -> StringUtils.isBlank(schema.domain());

  SchemaTrigger isInValidType = schema -> Type.UNKNOWN.name().equalsIgnoreCase(schema.type());

  SchemaTrigger isPrimaryKeyNotExistsForEntity = schema -> {
    if (!schema.type().equalsIgnoreCase(Type.ENTITY.name())) {
      return false;
    }
    return schema.fieldList().stream().filter(Field::isPrimaryKey).count() != 1;
  };
}
