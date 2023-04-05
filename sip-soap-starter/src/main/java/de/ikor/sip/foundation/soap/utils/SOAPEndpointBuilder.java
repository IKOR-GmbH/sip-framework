package de.ikor.sip.foundation.soap.utils;

import java.util.Map;
import lombok.SneakyThrows;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.CxfEndpointBuilderFactory.CxfEndpointBuilder;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;

public class SOAPEndpointBuilder {

  private SOAPEndpointBuilder() {}

  @SneakyThrows
  public static CxfEndpointBuilder generateCXFEndpoint(
      Map<String, CxfEndpoint> cxfBeans,
      String serviceClassName,
      String serviceClassQualifiedName,
      String address) {
    if (cxfBeans.containsKey(serviceClassName)) {
      CxfEndpoint cxfEndpoint = cxfBeans.get(serviceClassName);
      if (cxfEndpoint.getServiceClass() == null)
        cxfEndpoint.setServiceClass(serviceClassQualifiedName);
      if (cxfEndpoint.getAddress() == null) cxfEndpoint.setAddress(address);
      // Our route building only works with payload mode
      cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);

      return StaticEndpointBuilders.cxf(String.format("bean:%s", serviceClassName));
    } else {
      return StaticEndpointBuilders.cxf(address)
          .serviceClass(serviceClassQualifiedName)
          .dataFormat(DataFormat.PAYLOAD);
    }
  }
}
