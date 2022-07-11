package de.ikor.sip.foundation.testkit.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.RouteProducerFactory;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.DefaultRouteProducer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.junit.jupiter.api.Test;

class SIPRouteProducerTemplateTest {

  @Test
  void GIVEN_exchangeAndDefaultRouteProducer_WHEN_requestOnRoute_THEN_expectEmptyExchange() {
    // arrange
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    Exchange exchange = mock(Exchange.class);
    SIPEndpointResolver sipEndpointResolver = mock(SIPEndpointResolver.class);
    RouteProducerFactory routeProducerFactory = mock(RouteProducerFactory.class);
    Endpoint endpoint = mock(Endpoint.class);
    SIPRouteProducerTemplate subject = new SIPRouteProducerTemplate(routeProducerFactory, sipEndpointResolver);
    when(sipEndpointResolver.resolveEndpoint(exchange)).thenReturn(endpoint);
    when(routeProducerFactory.resolveRouteProducer(endpoint)).thenReturn(new DefaultRouteProducer(camelContext));

    // act
    Exchange actualExchange = subject.requestOnRoute(exchange);

    // assert
    assertThat(actualExchange.getMessage().getBody()).isNull();
    assertThat(actualExchange.getMessage().getHeaders()).isEmpty();
  }
}
