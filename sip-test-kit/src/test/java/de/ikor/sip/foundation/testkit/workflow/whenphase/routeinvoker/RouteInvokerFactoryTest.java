package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.exception.NoRouteInvokerException;
import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.FileRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.*;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private static final String ROUTE_ID = "routeId";

  private RouteInvokerFactory subject;
  private Endpoint endpoint;
  private Exchange exchange;
  private Route route;

  @BeforeEach
  void setup() {
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    RestRouteInvoker restRouteInvoker =
        new RestRouteInvoker(mock(ProducerTemplate.class), camelContext);
    FileRouteInvoker fileRouteInvoker = new FileRouteInvoker(camelContext);
    Set<RouteInvoker> invokers =
        Set.of(restRouteInvoker, mock(CxfRouteInvoker.class), fileRouteInvoker);
    subject = new RouteInvokerFactory(invokers, camelContext);
    exchange = TestKitHelper.createEmptyExchange(camelContext);
    exchange.setProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, ROUTE_ID);
    route = mock(Route.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
  }

  @Test
  void GIVEN_restEndpoint_WHEN_resolveAndInvoke_THEN_RestRouteInvoker()
      throws NoRouteInvokerException {
    // arrange
    endpoint = mock(RestEndpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);

    // act
    RouteInvoker actual = subject.getInstance(exchange);

    // assert
    assertThat(actual).isInstanceOf(RestRouteInvoker.class);
  }

  @Test
  void GIVEN_fileEndpoint_WHEN_resolveAndInvoke_THEN_FileRouteInvoker()
      throws NoRouteInvokerException {
    // arrange
    endpoint = mock(FileEndpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);

    // act
    RouteInvoker actual = subject.getInstance(exchange);

    // assert
    assertThat(actual).isInstanceOf(FileRouteInvoker.class);
  }

  @Test
  void
      GIVEN_endpointWithoutRouteInvoker_WHEN_resolveAndInvoke_THEN_expectNoRouteInvokerException() {
    // arrange
    endpoint = mock(Endpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);

    // act & assert
    assertThrows(NoRouteInvokerException.class, () -> subject.getInstance(exchange));
  }
}
