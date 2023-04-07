package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.*;
import org.apache.camel.*;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.servlet.ServletConsumer;
import org.apache.camel.processor.Enricher;
import org.apache.camel.processor.PollEnricher;
import org.apache.camel.processor.SendProcessor;
import org.apache.camel.spi.CamelEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoutesRegistryTest {

  private static final String CONNECTOR_ID = "connectorId";
  private static final String GENERATED_ROUTE_ID = "sip-connector_connectorId_externalEndpoint";
  private static final String BASE_HTTP_URI = "http://baseUri";
  private RoutesRegistry subject;
  private DeclarationsRegistry declarationsRegistry;
  private DeclarationsRegistryTest.InboundConnectorMock connector;

  @BeforeEach
  void setup() {
    declarationsRegistry = mock(DeclarationsRegistry.class);
    subject = new RoutesRegistry(declarationsRegistry);
    connector = mock(DeclarationsRegistryTest.InboundConnectorMock.class);
    when(connector.getId()).thenReturn(CONNECTOR_ID);
  }

  @Test
  void GIVEN_connectorId_WHEN_getRouteIdByConnectorId_THEN_getProperRouteId() {
    // arrange
    subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connector);
    when(declarationsRegistry.getConnectorById(CONNECTOR_ID))
        .thenReturn(Optional.ofNullable(connector));

    // act
    String actualRouteId = subject.getRouteIdByConnectorId(CONNECTOR_ID);

    // assert
    assertThat(actualRouteId).isEqualTo(GENERATED_ROUTE_ID);
  }

  @Test
  void GIVEN_nonExistentConnectorId_WHEN_getRouteIdByConnectorId_THEN_expectNull() {
    // arrange
    when(declarationsRegistry.getConnectorById(CONNECTOR_ID))
        .thenReturn(Optional.ofNullable(connector));

    // act
    String actualRouteId = subject.getRouteIdByConnectorId(CONNECTOR_ID);

    // assert
    assertThat(actualRouteId).isNull();
  }

  @Test
  void GIVEN_nonExistentConnector_WHEN_getRouteIdByConnectorId_THEN_expectNull() {
    // act
    String actualRouteId = subject.getRouteIdByConnectorId(CONNECTOR_ID);

    // assert
    assertThat(actualRouteId).isNull();
  }

  @Test
  void
      GIVEN_alreadyExistingConnectorId_WHEN_generateRouteIdForConnector_THEN_SIPFrameworkInitializationException() {
    // arrange
    subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connector);

    // act & assert
    assertThatThrownBy(
            () -> {
              subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connector);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Can't build internal connector route with routeId '%s': routeId already exists",
            GENERATED_ROUTE_ID);
  }

  @Test
  void GIVEN_endpoint_WHEN_generateRouteInfoList_THEN_expectValidRouteDeclarativeStructureInfo() {
    // arrange
    subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connector);
    CamelEvent.CamelContextEvent event = mock(CamelEvent.CamelContextEvent.class);
    CamelContext camelContext = mock(ExtendedCamelContext.class);
    ProcessorProxyRegistry proxyRegistry = mock(ProcessorProxyRegistry.class);
    when(proxyRegistry.getProxies()).thenReturn(new HashMap<>());
    when(event.getContext()).thenReturn(camelContext);
    when(camelContext.getExtension(ProcessorProxyRegistry.class)).thenReturn(proxyRegistry);
    List<Route> routes = new ArrayList<>();
    Route route = mock(Route.class);
    when(route.getRouteId()).thenReturn(GENERATED_ROUTE_ID);

    Endpoint endpoint = mock(Endpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);
    when(endpoint.getEndpointBaseUri()).thenReturn(BASE_HTTP_URI);

    // SendProcessor case
    SendProcessor sendProcessor = mock(SendProcessor.class);
    when(sendProcessor.getEndpoint()).thenReturn(endpoint);

    List<Service> services = List.of(mock(ServletConsumer.class), sendProcessor);
    when(route.getServices()).thenReturn(services);
    routes.add(route);
    when(camelContext.getRoutes()).thenReturn(routes);

    // act
    subject.notify(event);
    List<RouteDeclarativeStructureInfo> actualEndpointAware =
        subject.generateRouteInfoList(endpoint);

    // assert
    assertThat(actualEndpointAware.get(0).getConnectorId()).isEqualTo(CONNECTOR_ID);
  }

  @Test
  void
      GIVEN_endpointAndSpecificProcessors_WHEN_generateRouteInfoList_THEN_expectValidRouteDeclarativeStructureInfo() {
    // arrange
    subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connector);
    CamelEvent.CamelContextEvent event = mock(CamelEvent.CamelContextEvent.class);
    CamelContext camelContext = mock(ExtendedCamelContext.class);
    ProcessorProxyRegistry proxyRegistry = mock(ProcessorProxyRegistry.class);
    when(proxyRegistry.getProxies()).thenReturn(new HashMap<>());
    when(event.getContext()).thenReturn(camelContext);
    when(camelContext.getExtension(ProcessorProxyRegistry.class)).thenReturn(proxyRegistry);
    List<Route> routes = new ArrayList<>();
    Route route = mock(Route.class);
    when(route.getRouteId()).thenReturn(GENERATED_ROUTE_ID);

    Endpoint endpoint = mock(Endpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);
    when(endpoint.getEndpointBaseUri()).thenReturn(BASE_HTTP_URI);

    // Enricher case
    Endpoint enricherEndpoint = mock(Endpoint.class);
    when(enricherEndpoint.getEndpointBaseUri()).thenReturn("uriForEnricher");
    Enricher enricher = mock(Enricher.class);
    when(enricher.getExpression()).thenReturn(ExpressionBuilder.simpleExpression("uriForEnricher"));

    // PollEnricher case (without expression)
    Endpoint pollEnricherEndpoint = mock(Endpoint.class);
    when(pollEnricherEndpoint.getEndpointBaseUri()).thenReturn("pollEnrich-1");
    PollEnricher pollEnricher = mock(PollEnricher.class);

    List<Service> services = List.of(enricher, pollEnricher);
    when(route.getServices()).thenReturn(services);
    routes.add(route);
    when(camelContext.getRoutes()).thenReturn(routes);

    // act
    subject.notify(event);
    List<RouteDeclarativeStructureInfo> actualEnricher =
        subject.generateRouteInfoList(pollEnricherEndpoint);
    List<RouteDeclarativeStructureInfo> actualPollEnricher =
        subject.generateRouteInfoList(pollEnricherEndpoint);

    // assert
    assertThat(actualEnricher.get(0).getConnectorId()).isEqualTo(CONNECTOR_ID);
    assertThat(actualPollEnricher.get(0).getConnectorId()).isEqualTo(CONNECTOR_ID);
  }
}
