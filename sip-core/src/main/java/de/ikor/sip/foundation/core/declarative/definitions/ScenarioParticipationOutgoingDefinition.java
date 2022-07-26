package de.ikor.sip.foundation.core.declarative.definitions;

import org.apache.camel.model.StepDefinition;

public interface ScenarioParticipationOutgoingDefinition {

  String getOutgoingEndpointUri();

  default void buildOutgoingConnectorHook(final StepDefinition definition) {}

  default void buildOutgoingConnectorAfterHook(final StepDefinition definition) {}
}
