package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private RouteInvokerFactory subject;
  private Endpoint endpoint;
  private Exchange inputExchange;

  @BeforeEach
  void setup() {
    Set<RouteInvoker> invokers = Set.of(mock(RestRouteInvoker.class), mock(CxfRouteInvoker.class));
    subject = new RouteInvokerFactory(invokers, mock(ExtendedCamelContext.class));
    inputExchange = mock(Exchange.class);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveAndInvoke_THEN_expectEmptyExchange() {
    // arrange
    endpoint = mock(Endpoint.class);

    // act
    Exchange actual = subject.resolveAndInvoke(inputExchange, endpoint);

    // assert
    assertThat(actual.getMessage().getBody()).isNull();
    assertThat(actual.getMessage().getHeaders()).isEmpty();
  }
}
