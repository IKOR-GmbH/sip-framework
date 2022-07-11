package de.ikor.sip.foundation.core.declarative.definitions;

import org.apache.camel.model.StepDefinition;

public interface ScenarioParticipationIncomingDefinition {

  String getIncomingEndpointUri();

  void buildIncomingConnectorHook(final StepDefinition definition);
}
