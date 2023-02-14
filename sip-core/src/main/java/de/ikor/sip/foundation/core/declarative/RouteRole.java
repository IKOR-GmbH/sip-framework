package de.ikor.sip.foundation.core.declarative;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum containing the different roles of a route. */
@AllArgsConstructor
public enum RouteRole {
  EXTERNAL_ENDPOINT("externalEndpoint"),
  CONNECTOR_REQUEST_ORCHESTRATION("requestOrchestration"),
  CONNECTOR_RESPONSE_ORCHESTRATION("responseOrchestration"),
  SCENARIO_HANDOFF("scenarioHandoff"),
  SCENARIO_TAKEOVER("scenarioTakeover");

  @Getter final String roleSuffixInRouteId;
}
