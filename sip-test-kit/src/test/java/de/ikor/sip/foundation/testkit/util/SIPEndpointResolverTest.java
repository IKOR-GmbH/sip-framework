package de.ikor.sip.foundation.testkit.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPEndpointResolverTest {

  private static final String CONNECTION_ALIAS = "alias";
  public static final String ENDPOINT_URI = "endpointuri";
  public static final String METHOD = "POST";

  SIPEndpointResolver subject;
  Exchange exchange;
  CamelContext camelContext;

  @BeforeEach
  void setup() {
    exchange = mock(Exchange.class);
    camelContext = mock(CamelContext.class);
    subject = new SIPEndpointResolver(camelContext);
  }

  @Test
  void When_resolveURI_With_NoRest_Expect_resolvedURI() {
    // arrange
    Route route = mock(Route.class);
    Endpoint endpoint = mock(Endpoint.class);
    when(exchange.getProperty("connectionAlias", String.class)).thenReturn(CONNECTION_ALIAS);
    when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(endpoint);
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    //    String resolvedURI = subject.resolveURI(exchange);

    // assert
    //    assertThat(resolvedURI).isEqualTo(ENDPOINT_URI);
  }

  @Test
  void When_resolveURI_With_Rest_Expect_resolvedURI() {
    // arrange
    Route route = mock(Route.class);
    RestEndpoint endpoint = mock(RestEndpoint.class);
    when(exchange.getProperty("connectionAlias", String.class)).thenReturn(CONNECTION_ALIAS);
    when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(endpoint);
    when(endpoint.getPath()).thenReturn(ENDPOINT_URI);
    when(endpoint.getMethod()).thenReturn(METHOD);
    String expectedURI = "rest:" + METHOD + ":" + ENDPOINT_URI;

    //    // act
    //    String resolvedURI = subject.resolveURI(exchange);
    //
    //    // assert
    //    assertThat(resolvedURI).isEqualTo(expectedURI);
  }
}
