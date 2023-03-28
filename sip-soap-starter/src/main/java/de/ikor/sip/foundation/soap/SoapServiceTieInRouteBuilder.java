package de.ikor.sip.foundation.soap;

import static de.ikor.sip.foundation.core.util.StreamHelper.typeFilter;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.soap.declarative.connector.SoapOperationInboundConnectorBase;
import de.ikor.sip.foundation.soap.utils.SOAPEndpointBuilder;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
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

    String soapServiceName = serviceClass.getSimpleName();

    final var routeChoices =
        from(SOAPEndpointBuilder.generateCXFEndpoint(
                applicationContext.getBeansOfType(CxfEndpoint.class),
                soapServiceName,
                serviceClass.getName(),
                soapServiceName))
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
        .throwException(
            SIPFrameworkException.class,
            "Operation ${header.operationName} is not supported in this adapter")
        .endChoice();
  }
}
