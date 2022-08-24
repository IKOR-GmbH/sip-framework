package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import org.apache.camel.*;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.Test;

class RestRouteInvokerTest {

  private static final String ROUTE_ID = "routeId";

  @Test
  void GIVEN_mockedExchangeAndEndpoint_WHEN_executeTask_THEN_verifySendingToGoodEndpointUri() {
    // arrange
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
    RestRouteInvoker subject = new RestRouteInvoker(producerTemplate, camelContext);
    RestEndpoint restEndpoint = mock(RestEndpoint.class);
    Exchange exchange = mock(Exchange.class);
    when(restEndpoint.getMethod()).thenReturn("post");
    when(restEndpoint.getPath()).thenReturn("test");
    when(producerTemplate.send("rest:post:test", exchange)).thenReturn(exchange);

    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class)).thenReturn(ROUTE_ID);
    Route route = mock(Route.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(restEndpoint);

    // act
    subject.invoke(exchange);

    // assert
    verify(producerTemplate, times(1)).send("rest:post:test", exchange);
  }
}
