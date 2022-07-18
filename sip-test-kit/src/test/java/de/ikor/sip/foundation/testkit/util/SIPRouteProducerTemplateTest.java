package de.ikor.sip.foundation.testkit.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvokerFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPRouteProducerTemplateTest {

  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
  }

  @Test
  void GIVEN_exchange_WHEN_requestOnRoute_THEN_expectEmptyExchange() {
    // arrange
    Exchange inputExchange = mock(Exchange.class);
    SIPEndpointResolver sipEndpointResolver = mock(SIPEndpointResolver.class);
    RouteInvokerFactory routeInvokerFactory = mock(RouteInvokerFactory.class);
    Endpoint endpoint = mock(Endpoint.class);
    SIPRouteProducerTemplate subject =
        new SIPRouteProducerTemplate(routeInvokerFactory, sipEndpointResolver);
    when(sipEndpointResolver.resolveEndpoint(inputExchange)).thenReturn(endpoint);
    Exchange emptyExchange = createEmptyExchange();
    when(routeInvokerFactory.resolveAndInvoke(inputExchange, endpoint)).thenReturn(emptyExchange);

    // act
    Exchange actualExchange = subject.requestOnRoute(inputExchange);

    // assert
    assertThat(actualExchange.getMessage().getBody()).isNull();
    assertThat(actualExchange.getMessage().getHeaders()).isEmpty();
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
