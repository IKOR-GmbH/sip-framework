package de.ikor.sip.foundation.core.declarative.definitions;

import org.apache.camel.model.StepDefinition;

public interface ScenarioParticipationOutgoingDefinition {

  String getOutgoingEndpointUri();

  void buildOutgoingConnectorHook(final StepDefinition definition);
}
