package de.ikor.sip.foundation.core.declarative.orchestation;

import org.apache.camel.model.RouteDefinition;

public interface EndpointOrchestrationInfo extends OrchestrationInfo {

  RouteDefinition getRouteDefinition();
}
