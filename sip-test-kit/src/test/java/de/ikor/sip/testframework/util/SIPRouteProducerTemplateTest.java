package de.ikor.sip.testframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;

class SIPRouteProducerTemplateTest {

  public static final String URI = "uri";

  @Test
  void When_requestOnRoute_Expect_ExchangeReturned() {
    // arrange
    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
    SIPEndpointResolver sipEndpointResolver = mock(SIPEndpointResolver.class);
    Exchange exchange = mock(Exchange.class);
    Exchange expected = mock(Exchange.class);
    when(sipEndpointResolver.resolveURI(exchange)).thenReturn(URI);
    when(producerTemplate.send(URI, exchange)).thenReturn(expected);
    SIPRouteProducerTemplate sipRouteProducerTemplate =
        new SIPRouteProducerTemplate(producerTemplate, sipEndpointResolver);

    // act
    Exchange result = sipRouteProducerTemplate.requestOnRoute(exchange);

    // assert
    verify(producerTemplate, times(1)).send(URI, exchange);
    assertThat(result).isEqualTo(expected);
  }
}
