package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.CxfRouteProducer;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.DefaultRouteProducer;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.RestRouteProducer;
import org.apache.camel.Endpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteProducerFactoryTest {

  private RouteProducerFactory subject;
  private Endpoint endpoint;

  @BeforeEach
  void setup() {
    DefaultRouteProducer defaultRouteProducer = mock(DefaultRouteProducer.class);
    RestRouteProducer restRouteProducer = mock(RestRouteProducer.class);
    CxfRouteProducer cxfRouteProducer = mock(CxfRouteProducer.class);
    subject = new RouteProducerFactory(defaultRouteProducer, restRouteProducer, cxfRouteProducer);
  }

  @Test
  void GIVEN_baseEndpoint_WHEN_resolveRouteProducer_THEN_expectDefaultRouteProducer() {
    // arrange
    endpoint = mock(Endpoint.class);

    // act
    RouteProducer actual = subject.resolveRouteProducer(endpoint);

    // assert
    assertThat(actual).isInstanceOf(DefaultRouteProducer.class);
  }

  @Test
  void GIVEN_RestEndpoint_WHEN_resolveRouteProducer_THEN_expectRestRouteProducer() {
    // arrange
    endpoint = mock(RestEndpoint.class);

    // act
    RouteProducer actual = subject.resolveRouteProducer(endpoint);

    // assert
    assertThat(actual).isInstanceOf(RestRouteProducer.class);
  }
}
