package de.ikor.sip.foundation.core.declarative.orchestation;

import java.util.Optional;
import org.apache.camel.model.RouteDefinition;

public interface ConnectorOrchestrationInfo extends OrchestrationInfo {

  RouteDefinition getRequestRouteDefinition();

  Optional<RouteDefinition> getResponseRouteDefinition();
}
