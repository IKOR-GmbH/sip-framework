package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.junit.jupiter.api.Test;

class DefaultRouteProducerTest {

  @Test
  void GIVEN_mockedExchangeAndEndpoint_WHEN_executeTask_THEN_expectEmptyExchange() {
    // arrange
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    DefaultRouteProducer subject = new DefaultRouteProducer(camelContext);
    Exchange exchange = mock(Exchange.class);
    Endpoint endpoint = mock(Endpoint.class);

    // act
    Exchange actualExchange = subject.executeTask(exchange, endpoint);

    // assert
    assertThat(actualExchange.getMessage().getBody()).isNull();
    assertThat(actualExchange.getMessage().getHeaders()).isEmpty();
  }
}
