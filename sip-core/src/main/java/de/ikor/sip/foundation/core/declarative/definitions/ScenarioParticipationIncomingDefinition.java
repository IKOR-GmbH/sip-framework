package de.ikor.sip.foundation.core.declarative.definitions;

import org.apache.camel.model.StepDefinition;

public interface ScenarioParticipationIncomingDefinition {

  String getIncomingEndpointUri();

  default void buildIncomingConnectorHook(final StepDefinition definition) {}

  default void buildIncomingConnectorAfterHook(final StepDefinition definition) {}
}
