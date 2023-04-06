package de.ikor.sip.foundation.core.declarative.orchestration.dsl;

import org.apache.camel.model.RouteDefinition;

public interface RouteFromDslBuilder<T> {

  T getDefinitionElement();

  RouteDefinition buildRouteFromDefinition(
      final T definition, final RouteDefinition routeDefinition);
}
