package de.ikor.sip.foundation.testkit.util;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Route;

/** Util class for resolving camel endpoints */
public class SIPEndpointResolver {

  private SIPEndpointResolver() {}

  /**
   * Get camel endpoint based on route id
   *
   * @param routeId for fetching the route
   * @param camelContext in which routes are defined
   * @return {@link Endpoint}
   */
  public static Endpoint resolveEndpoint(String routeId, CamelContext camelContext) {
    Route route = camelContext.getRoute(routeId);
    if (route == null) {
      throw new IllegalArgumentException("Route with id " + routeId + " was not found");
    }
    return route.getEndpoint();
  }
}
