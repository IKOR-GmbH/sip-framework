package de.ikor.sip.foundation.core.declarative.orchestation;

import org.apache.camel.model.RouteDefinition;

import java.util.Optional;

public interface ConnectorOrchestrationInfo extends OrchestrationInfo {

    RouteDefinition getRequestRouteDefinition();

    Optional<RouteDefinition> getResponseRouteDefinition();

}
