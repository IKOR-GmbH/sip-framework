package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.CxfRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.FileRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.RestRouteInvoker;
import java.util.Set;
import org.apache.camel.*;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteInvokerFactoryTest {

  private RouteInvokerFactory subject;
  private Endpoint endpoint;

  @BeforeEach
  void setup() {
    ExtendedCamelContext camelContext1 = mock(ExtendedCamelContext.class);
    RestRouteInvoker restRouteInvoker = new RestRouteInvoker(mock(ProducerTemplate.class));
    FileRouteInvoker fileRouteInvoker = new FileRouteInvoker(camelContext1);
    Set<RouteInvoker> invokers =
        Set.of(restRouteInvoker, mock(CxfRouteInvoker.class), fileRouteInvoker);
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    subject = new RouteInvokerFactory(invokers, camelContext);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveAndInvoke_THEN_DefaultRouteInvoker() {
    // arrange
    endpoint = mock(Endpoint.class);

    // act
    RouteInvoker actual = subject.getInstance(endpoint);

    // assert
    assertThat(actual).isInstanceOf(DefaultRouteInvoker.class);
  }

  @Test
  void GIVEN_restEndpoint_WHEN_resolveAndInvoke_THEN_RestRouteInvoker() {
    // arrange
    endpoint = mock(RestEndpoint.class);

    // act
    RouteInvoker actual = subject.getInstance(endpoint);

    // assert
    assertThat(actual).isInstanceOf(RestRouteInvoker.class);
  }

  @Test
  void GIVEN_fileEndpoint_WHEN_resolveAndInvoke_THEN_FileRouteInvoker() {
    // arrange
    endpoint = mock(FileEndpoint.class);

    // act
    RouteInvoker actual = subject.getInstance(endpoint);

    // assert
    assertThat(actual).isInstanceOf(FileRouteInvoker.class);
  }
}
