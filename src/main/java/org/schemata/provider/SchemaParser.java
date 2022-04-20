package org.schemata.provider;

import java.util.List;
import org.schemata.domain.Schema;


public interface SchemaParser {

  List<Schema> getSchemaList(String path);
}
