package de.ikor.sip.foundation.soap;

import static de.ikor.sip.foundation.core.util.StreamHelper.typeFilter;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.soap.declarative.connector.SoapOperationInboundConnectorBase;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SoapServiceTieInRouteBuilder extends RouteBuilder {

  private final DeclarationsRegistryApi declarationRegistry;

  private final RoutesRegistry routesRegistry;

  private final ApplicationContext applicationContext;

  @Override
  public void configure() {
    final var inboundConnectors =
        declarationRegistry.getInboundConnectors().stream()
            .flatMap(typeFilter(SoapOperationInboundConnectorBase.class))
            .collect(
                Collectors.groupingBy(SoapOperationInboundConnectorBase::getServiceInterfaceClass));
    inboundConnectors
        .entrySet()
        .forEach(
            classListEntry ->
                configureAndTieCxfEndpoint(classListEntry.getKey(), classListEntry.getValue()));
  }

  @SuppressWarnings("rawtypes")
  private void configureAndTieCxfEndpoint(
      final Class serviceClass, final Collection<SoapOperationInboundConnectorBase> connectors) {

    Map<String, CxfEndpoint> cxfEndpointBeans =
        applicationContext.getBeansOfType(CxfEndpoint.class);
    String soapServiceName = serviceClass.getSimpleName();
    String soapServiceQualifiedName = serviceClass.getName();

    final var routeChoices =
        (cxfEndpointBeans.containsKey(soapServiceName)
                ? generateCXFBeanEndpoint(
                    cxfEndpointBeans, soapServiceName, soapServiceQualifiedName)
                : generateCXFDefaultEndpoint(soapServiceName, soapServiceQualifiedName))
            .routeId(routesRegistry.generateRouteIdForSoapService(soapServiceName))
            .log(LoggingLevel.TRACE, "Received SOAP request for ${header.operationName}")
            .choice();
    connectors.forEach(
        connector ->
            routeChoices
                .when(
                    header(CxfConstants.OPERATION_NAME)
                        .isEqualTo(connector.getServiceOperationName()))
                .log(
                    LoggingLevel.TRACE,
                    "Routing SOAP request for ${header.operationName} to ${header.CamelCxfEndpointUri}")
                .to(connector.getSoapServiceTieInEndpoint()));
    routeChoices
        .otherwise()
        .throwException(new SIPFrameworkException("Operation is not supported in this adapter"))
        .endChoice();
  }

  @SneakyThrows
  RouteDefinition generateCXFBeanEndpoint(
      Map<String, CxfEndpoint> cxfBeans,
      String serviceClassName,
      String serviceClassQualifiedName) {
    CxfEndpoint cxfEndpoint = cxfBeans.get(serviceClassName);
    if (cxfEndpoint.getServiceClass() == null)
      cxfEndpoint.setServiceClass(serviceClassQualifiedName);
    if (cxfEndpoint.getAddress() == null) cxfEndpoint.setAddress(serviceClassName);
    // Our route building only works with payload mode
    cxfEndpoint.setDataFormat(DataFormat.PAYLOAD);

    return from(String.format("cxf:bean:%s", serviceClassName));
  }

  RouteDefinition generateCXFDefaultEndpoint(String address, String serviceClassName) {
    return from(
        StaticEndpointBuilders.cxf(address)
            .serviceClass(serviceClassName)
            .dataFormat(DataFormat.PAYLOAD));
  }
}
