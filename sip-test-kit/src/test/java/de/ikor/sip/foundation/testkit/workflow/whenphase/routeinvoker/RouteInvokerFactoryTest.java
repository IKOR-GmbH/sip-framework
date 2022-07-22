package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.util.SIPEndpointResolver;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private RouteInvokerFactory subject;
  private Endpoint endpoint;
  private Exchange inputExchange;
  private SIPEndpointResolver sipEndpointResolver;

  @BeforeEach
  void setup() {
    RestRouteInvoker restRouteInvoker = new RestRouteInvoker(mock(ProducerTemplate.class));
    Set<RouteInvoker> invokers = Set.of(restRouteInvoker, mock(CxfRouteInvoker.class));
    sipEndpointResolver = mock(SIPEndpointResolver.class);
    subject =
        new RouteInvokerFactory(invokers, sipEndpointResolver, mock(ExtendedCamelContext.class));
    inputExchange = mock(Exchange.class);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveAndInvoke_THEN_DefaultRouteInvoker() {
    // arrange
    endpoint = mock(Endpoint.class);
    when(sipEndpointResolver.resolveEndpoint(inputExchange)).thenReturn(endpoint);

    // act
    RouteInvoker actual = subject.getInstance(inputExchange);

    // assert
    assertThat(actual).isInstanceOf(DefaultRouteInvoker.class);
  }

  @Test
  void GIVEN_restEndpoint_WHEN_resolveAndInvoke_THEN_RestRouteInvoker() {
    // arrange
    endpoint = mock(RestEndpoint.class);
    when(sipEndpointResolver.resolveEndpoint(inputExchange)).thenReturn(endpoint);

    // act
    RouteInvoker actual = subject.getInstance(inputExchange);

    // assert
    assertThat(actual).isInstanceOf(RestRouteInvoker.class);
  }
}
