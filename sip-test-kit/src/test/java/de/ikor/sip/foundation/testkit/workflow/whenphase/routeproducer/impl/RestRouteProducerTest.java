package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.Test;

class RestRouteProducerTest {

  @Test
  void GIVEN_mockedExchangeAndEndpoint_WHEN_executeTask_THEN_verifySendingToGoodEndpointUri() {
    // arrange
    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
    RestRouteProducer subject = new RestRouteProducer(producerTemplate);
    RestEndpoint restEndpoint = mock(RestEndpoint.class);
    Exchange exchange = mock(Exchange.class);
    when(restEndpoint.getMethod()).thenReturn("post");
    when(restEndpoint.getPath()).thenReturn("test");
    when(producerTemplate.send("rest:post:test", exchange)).thenReturn(exchange);

    // act
    subject.executeTask(exchange, restEndpoint);

    // assert
    verify(producerTemplate, times(1)).send("rest:post:test", exchange);
  }
}
