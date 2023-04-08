package de.ikor.sip.foundation.soap.utils;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Map;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.CxfEndpointBuilderFactory.CxfEndpointBuilder;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.commons.lang3.StringUtils;

public class SOAPEndpointBuilder {

  private SOAPEndpointBuilder() {}

  public static CxfEndpointBuilder generateCXFEndpoint(
      String connectorID,
      Map<String, CxfEndpoint> cxfBeans,
      String serviceClassName,
      String serviceClassQualifiedName,
      String address) {

    if (cxfBeans.containsKey(serviceClassName)) {

      CxfEndpoint cxfEndpoint = cxfBeans.get(serviceClassName);
      if (cxfEndpoint.getServiceClass() == null) {
        try {
          cxfEndpoint.setServiceClass(serviceClassQualifiedName);
        } catch (ClassNotFoundException e) {
          throw new SIPFrameworkInitializationException(
              String.format(
                  "Service class '%s' used in the soap connector '%s' can not be found",
                  serviceClassQualifiedName, connectorID),
              e);
        }
      }
      if (StringUtils.isBlank(cxfEndpoint.getAddress())) {
        if (StringUtils.isBlank(address)) {
          throw new SIPFrameworkInitializationException(
              String.format(
                  "CXFEndpoint bean '%s' is defined but the SOAP address is undefined. "
                      + "Please use 'setAddress()' in the CXFEndpoint bean or @Override the 'getServiceAddress()' method in the connector '%s'",
                  serviceClassName, connectorID));
        }
        cxfEndpoint.setAddress(address);
      }
      // Our route building only works with payload mode
      cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);

      return StaticEndpointBuilders.cxf(String.format("bean:%s", serviceClassName));
    } else {

      if (StringUtils.isBlank(address)) {
        throw new SIPFrameworkInitializationException(
            String.format(
                "Connector '%s' doesn't have a defined address. Please @Override the 'getServiceAddress()' method"
                    + " or define a CXFBean with name '%s'",
                connectorID, serviceClassName));
      }
      return StaticEndpointBuilders.cxf(address)
          .serviceClass(serviceClassQualifiedName)
          .dataFormat(DataFormat.PAYLOAD);
    }
  }
}
