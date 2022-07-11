package de.ikor.sip.foundation.testkit.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPEndpointResolverTest {

  private static final String CONNECTION_ALIAS = "alias";

  private SIPEndpointResolver subject;
  private Exchange exchange;
  private CamelContext camelContext;

  @BeforeEach
  void setup() {
    exchange = mock(Exchange.class);
    camelContext = mock(CamelContext.class);
    subject = new SIPEndpointResolver(camelContext);
  }

  @Test
  void GIVEN_goodConnectionAlias_WHEN_resolveEndpoint_THEN_returnEndpoint() {
    // arrange
    Route route = mock(Route.class);
    Endpoint expectedEndpoint = mock(Endpoint.class);
    when(exchange.getProperty("connectionAlias", String.class)).thenReturn(CONNECTION_ALIAS);
    when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(expectedEndpoint);

    // act
    Endpoint actualEndpoint = subject.resolveEndpoint(exchange);

    // assert
    assertThat(actualEndpoint).isEqualTo(expectedEndpoint);
  }

  @Test
  void GIVEN_noConnectionAlias_WHEN_resolveEndpoint_THEN_IllegalArgumentException() {
    // act & assert
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> subject.resolveEndpoint(exchange));
  }
}
