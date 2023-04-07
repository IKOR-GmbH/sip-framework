package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.*;
import org.apache.camel.*;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.component.servlet.ServletConsumer;
import org.apache.camel.spi.CamelEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoutesRegistryTest {

  private static final String CONNECTOR_ID = "connectorId";

  private static final String GENERATED_ROUTE_ID = "sip-connector_connectorId_externalEndpoint";

  private RoutesRegistry subject;

  private DeclarationsRegistry declarationsRegistry;

  private ConnectorMock connector;

  private class ConnectorMock extends GenericInboundConnectorBase {

    @Override
    protected EndpointConsumerBuilder defineInitiatingEndpoint() {
      return null;
    }
  }

  @BeforeEach
  void setup() {
    declarationsRegistry = mock(DeclarationsRegistry.class);
    subject = new RoutesRegistry(declarationsRegistry);
    connector = mock(ConnectorMock.class);
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
    when(endpoint.getEndpointBaseUri()).thenReturn("http://baseUri");

    List<Service> services = Arrays.asList(mock(ServletConsumer.class), endpoint);
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
}
