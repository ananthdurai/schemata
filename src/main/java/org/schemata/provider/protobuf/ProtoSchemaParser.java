package org.schemata.provider.protobuf;

import com.google.protobuf.Descriptors;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.schemata.domain.Schema;
import org.schemata.exception.SchemaParserException;
import org.schemata.provider.SchemaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parse .desc proto descriptor file
 */
public class ProtoSchemaParser implements SchemaParser {

  private static final Logger logger = LoggerFactory.getLogger(ProtoSchemaParser.class);

  @Override
  public List<Schema> getSchemaList(String path)
      throws SchemaParserException {
    try {
      var stream = new FileInputStream(path);
      var loader = new ProtoFileDescriptorSetLoader(stream);
      var descriptors = loader.loadDescriptors();
      return new ProtoProcessor().parse(descriptors);
    } catch (IOException | Descriptors.DescriptorValidationException e) {
      logger.error("Error finding file:", e);
      throw new SchemaParserException("Error parsing Proto Schema", e);
    }
  }
}
