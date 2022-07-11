package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.CxfRouteProducer;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.DefaultRouteProducer;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.RestRouteProducer;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Endpoint;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.stereotype.Component;

/**
 * Factory class which produces RouteProducer based on Endpoint type
 */
@Component
@RequiredArgsConstructor
public class RouteProducerFactory {

  private final CxfRouteProducer soapRouteProducer;
  private final RestRouteProducer restRouteProducer;
  private final DefaultRouteProducer defaultRouteProducer;

  /**
   *
   *
   * @param endpoint {@link Endpoint} that is route consumer
   * @return {@link RouteProducer} RouteProducer for task executing
   */
  public RouteProducer resolveRouteProducer(Endpoint endpoint) {
    if (endpoint instanceof CxfEndpoint) {
      return soapRouteProducer;
    }
    if (endpoint instanceof RestEndpoint) {
      return restRouteProducer;
    }
    return defaultRouteProducer;
  }
}
