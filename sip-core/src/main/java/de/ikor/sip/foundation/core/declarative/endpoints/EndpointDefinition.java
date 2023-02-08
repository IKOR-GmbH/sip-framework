package de.ikor.sip.foundation.core.declarative.endpoints;

import org.apache.camel.model.RouteDefinition;

// TODO: Missing java docs, when the inheritance structure is final add this.
public interface EndpointDefinition {
  String getEndpointId();

  EndpointType getEndpointType();

  /**
   * Define response handling
   *
   * @param routeDefinition The definition to which handlers are appended
   */
  void configureAfterResponse(RouteDefinition routeDefinition);
}
