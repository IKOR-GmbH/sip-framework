package de.ikor.sip.foundation.soap;

import static de.ikor.sip.foundation.core.util.StreamHelper.typeFilter;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.soap.declarative.connector.SoapOperationInboundConnectorBase;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.component.cxf.common.CxfPayload;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SoapServiceTieInRouteBuilder extends RouteBuilder {

  private final DeclarationsRegistryApi declarationRegistry;

  private final RoutesRegistry routesRegistry;

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
    final var routeChoices =
        from(StaticEndpointBuilders.cxf(
                String.format(
                    "%s?serviceClass=%s&dataFormat=PAYLOAD",
                    serviceClass.getSimpleName(), serviceClass.getName())))
            .routeId(routesRegistry.generateRouteIdForSoapService(serviceClass.getSimpleName()))
            .log(LoggingLevel.TRACE, "Received SOAP request for ${header.operationName}")
            .transform()
            .body(CxfPayload.class, payload -> payload.getBody().get(0))
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
}
