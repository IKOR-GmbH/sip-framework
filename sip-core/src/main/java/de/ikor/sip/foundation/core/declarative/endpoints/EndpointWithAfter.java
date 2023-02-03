package de.ikor.sip.foundation.core.declarative.endpoints;

import org.apache.camel.model.RouteDefinition;

public interface EndpointWithAfter {
  void configureAfterResponse(RouteDefinition routeDefinition);
}
