package org.schemata.validate;

import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.schemata.domain.Field;
import org.schemata.domain.Schema;
import org.schemata.schema.SchemataBuilder;


public interface SchemaTrigger extends Predicate<Schema> {

  SchemaTrigger isDescriptionEmpty = schema -> StringUtils.isBlank(schema.description());

  SchemaTrigger isOwnerEmpty = schema -> StringUtils.isBlank(schema.owner());

  SchemaTrigger isDomainEmpty = schema -> StringUtils.isBlank(schema.domain());

  SchemaTrigger isInValidType = schema -> SchemataBuilder.Type.UNKNOWN.name().equalsIgnoreCase(schema.type());

  SchemaTrigger isPrimaryKeyNotExistsForEntity = schema -> {
    if (!schema.type().equalsIgnoreCase(SchemataBuilder.Type.ENTITY.name())) {
      return false;
    }
    return schema.fieldList().stream().filter(Field::isPrimaryKey).count() != 1;
  };
}
