package de.ikor.sip.foundation.soap.utils;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

import de.ikor.sip.foundation.core.declarative.model.MarshallerDefinition;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.spi.DataFormat;

public interface OutboundSOAPMarshallerDefinition extends MarshallerDefinition {
  static MarshallerDefinition forDataFormatWithOperationAndAddress(
      final DataFormat dataFormat, String operationName, String address) {
    return route ->
        route
            .setHeader(CxfConstants.OPERATION_NAME, constant(operationName))
            .setHeader(CxfConstants.DESTINATION_OVERRIDE_URL, constant(address))
            .marshal(dataFormat);
  }
}
