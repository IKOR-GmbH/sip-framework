package de.ikor.sip.foundation.testkit.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.util.SIPRouteProducerTemplate;
import org.apache.camel.*;

class SIPRouteProducerTemplateTest {

  // @Test
  void requestOnRoute() {
    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
    CamelContext camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    Endpoint endpoint = mock(Endpoint.class);
    Exchange input = mock(Exchange.class);
    Exchange expected = mock(Exchange.class);
    Message in = mock(Message.class);
    Message message = mock(Message.class);
    SIPRouteProducerTemplate sipRouteProducerTemplate =
        new SIPRouteProducerTemplate(producerTemplate, null);

    when(camelContext.getRoute(anyString()).getEndpoint()).thenReturn(endpoint);
    when(endpoint.createExchange()).thenReturn(input);
    when(message.getBody(String.class)).thenReturn("test");
    when(input.getIn()).thenReturn(in);
    when(expected.getMessage()).thenReturn(message);
    when(producerTemplate.send(endpoint, input)).thenReturn(expected);

    Exchange result = sipRouteProducerTemplate.requestOnRoute(input);

    assertEquals(expected, result);
  }
}
