package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.*;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private static final String CONNECTION_ALIAS = "alias";

  private RouteInvokerFactory subject;
  private Endpoint endpoint;
  private Exchange inputExchange;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    RestRouteInvoker restRouteInvoker = new RestRouteInvoker(mock(ProducerTemplate.class));
    Set<RouteInvoker> invokers = Set.of(restRouteInvoker, mock(CxfRouteInvoker.class));
    camelContext = mock(ExtendedCamelContext.class);
    subject = new RouteInvokerFactory(invokers, camelContext);
    inputExchange = mock(Exchange.class);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveAndInvoke_THEN_DefaultRouteInvoker() {
    // arrange
    RouteInvokerFactory spySubject = spy(subject);
    endpoint = mock(Endpoint.class);
    doReturn(endpoint).when(spySubject).resolveEndpoint(any());

    // act
    RouteInvoker actual = spySubject.getInstance(inputExchange);

    // assert
    assertThat(actual).isInstanceOf(DefaultRouteInvoker.class);
  }

  @Test
  void GIVEN_restEndpoint_WHEN_resolveAndInvoke_THEN_RestRouteInvoker() {
    // arrange
    RouteInvokerFactory spySubject = spy(subject);
    endpoint = mock(RestEndpoint.class);
    doReturn(endpoint).when(spySubject).resolveEndpoint(any());

    // act
    RouteInvoker actual = spySubject.getInstance(inputExchange);

    // assert
    assertThat(actual).isInstanceOf(RestRouteInvoker.class);
  }

  @Test
  void GIVEN_goodConnectionAlias_WHEN_resolveEndpoint_THEN_returnEndpoint() {
    // arrange
    Route route = mock(Route.class);
    Endpoint expectedEndpoint = mock(Endpoint.class);
    when(inputExchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class))
        .thenReturn(CONNECTION_ALIAS);
    when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(expectedEndpoint);

    // act
    Endpoint actualEndpoint = subject.resolveEndpoint(inputExchange);

    // assert
    assertThat(actualEndpoint).isEqualTo(expectedEndpoint);
  }

  @Test
  void GIVEN_noConnectionAlias_WHEN_resolveEndpoint_THEN_IllegalArgumentException() {
    // act & assert
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> subject.resolveEndpoint(inputExchange));
  }
}
