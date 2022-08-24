package de.ikor.sip.foundation.testkit.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.Endpoint;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPEndpointResolverTest {

  private static final String ROUTE_ID = "routeId";

  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
  }

  @Test
  void GIVEN_route_WHEN_resolveEndpoint_THEN_expectEndpoint() {
    // assert
    Route route = mock(Route.class);
    Endpoint expectedEndpoint = mock(Endpoint.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(expectedEndpoint);

    // act
    Endpoint actualEndpoint = SIPEndpointResolver.resolveEndpoint(ROUTE_ID, camelContext);

    // arrange
    assertThat(actualEndpoint).isEqualTo(expectedEndpoint);
  }

  @Test
  void GIVEN_noRoute_WHEN_resolveEndpoint_THEN_expectIllegalArgumentException() {
    // act & arrange
    assertThrows(
        IllegalArgumentException.class,
        () -> SIPEndpointResolver.resolveEndpoint(ROUTE_ID, camelContext));
  }
}
