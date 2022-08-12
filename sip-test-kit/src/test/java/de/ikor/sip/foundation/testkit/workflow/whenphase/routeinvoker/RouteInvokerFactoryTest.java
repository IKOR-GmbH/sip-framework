package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.*;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private RouteInvokerFactory subject;
  private Endpoint endpoint;
  private Exchange inputExchange;

  @BeforeEach
  void setup() {
    RestRouteInvoker restRouteInvoker = new RestRouteInvoker(mock(ProducerTemplate.class));
    Set<RouteInvoker> invokers = Set.of(restRouteInvoker, mock(CxfRouteInvoker.class));
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    subject = new RouteInvokerFactory(invokers, camelContext);
    inputExchange = mock(Exchange.class);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveAndInvoke_THEN_DefaultRouteInvoker() {
    // arrange
    endpoint = mock(Endpoint.class);

    // act
    RouteInvoker actual = subject.getInstance(inputExchange, endpoint);

    // assert
    assertThat(actual).isInstanceOf(DefaultRouteInvoker.class);
  }

  @Test
  void GIVEN_restEndpoint_WHEN_resolveAndInvoke_THEN_RestRouteInvoker() {
    // arrange
    endpoint = mock(RestEndpoint.class);

    // act
    RouteInvoker actual = subject.getInstance(inputExchange, endpoint);

    // assert
    assertThat(actual).isInstanceOf(RestRouteInvoker.class);
  }
}
