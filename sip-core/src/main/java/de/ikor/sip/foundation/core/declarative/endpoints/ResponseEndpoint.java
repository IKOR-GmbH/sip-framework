package de.ikor.sip.foundation.core.declarative.endpoints;

import org.apache.camel.model.RouteDefinition;

/** Handler for endpoints that may return a response */
public interface ResponseEndpoint {
  /**
   * Define response handling
   *
   * @param routeDefinition The definition to which handlers are appended
   */
  void configureAfterResponse(RouteDefinition routeDefinition);
}
