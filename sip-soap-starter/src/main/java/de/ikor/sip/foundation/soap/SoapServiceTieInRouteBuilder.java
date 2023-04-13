package de.ikor.sip.foundation.soap;

import static de.ikor.sip.foundation.core.util.StreamHelper.typeFilter;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import de.ikor.sip.foundation.soap.declarative.connector.SoapOperationInboundConnectorBase;
import de.ikor.sip.foundation.soap.utils.SOAPEndpointBuilder;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.jws.WebMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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

    checkImplementedMethods(serviceClass, connectors);

    String soapServiceName = serviceClass.getSimpleName();

    final var routeChoices =
        from(SOAPEndpointBuilder.generateCXFEndpoint(
                soapServiceName,
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

  @SuppressWarnings("rawtypes")
  private void checkImplementedMethods(
      final Class serviceInterface,
      final Collection<SoapOperationInboundConnectorBase> connectors) {

    Map<SoapOperationInboundConnectorBase, String> connectorImplementedOperations =
        connectors.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    SoapOperationInboundConnectorBase::getServiceOperationName));

    List<String> serviceClassOperations =
        Arrays.stream(serviceInterface.getMethods())
            .filter(method -> method.isAnnotationPresent(WebMethod.class))
            .map(Method::getName)
            .toList();

    connectorImplementedOperations.values().stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .filter(op -> op.getValue() > 1)
        .map(Map.Entry::getKey)
        .forEach(
            op -> {
              throw SIPFrameworkInitializationException.init(
                  "There are multiple Inbound SOAP Connectors implementing operation \"%s\" for Service \"%s\"",
                  op, serviceInterface.getName());
            });
    connectorImplementedOperations.entrySet().stream()
        .filter(conn -> !serviceClassOperations.contains(conn.getValue()))
        .forEach(
            conn ->
                log.warn(
                    "SIP WARNING - Inbound SOAP Connector \"{}\" implements an operation \"{}\" that doesn't exist in the Service \"{}\"",
                    conn.getKey().getClass().getName(),
                    conn.getValue(),
                    serviceInterface.getName()));

    serviceClassOperations.stream()
        .filter(op -> !connectorImplementedOperations.containsValue(op))
        .forEach(
            op ->
                log.warn(
                    "SIP WARNING - There is no Inbound SOAP Connector implementing an operation \"{}\" from Service \"{}\"",
                    op,
                    serviceInterface.getName()));
  }
}
