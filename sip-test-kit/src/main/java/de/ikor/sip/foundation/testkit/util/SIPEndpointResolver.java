package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.springframework.stereotype.Service;

/** Helper Service which resolves an Endpoint for a route during runtime. */
@Service
@RequiredArgsConstructor
public class SIPEndpointResolver {

  private final CamelContext camelContext;

  /**
   * Resolve Endpoint for the Exchange
   *
   * @param exchange for which the first Endpoint needs to be returned
   * @return Endpoint
   */
  public Endpoint resolveEndpoint(Exchange exchange) {
    String routeId = exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class);
    Route route = camelContext.getRoute(routeId);
    if (route == null) {
      throw new IllegalArgumentException("Route with id " + routeId + " was not found");
    }
    return route.getEndpoint();
  }
}
