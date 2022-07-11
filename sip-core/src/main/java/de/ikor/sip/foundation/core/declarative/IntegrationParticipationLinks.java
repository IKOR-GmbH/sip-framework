package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.definitions.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationIncomingDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationOutgoingDefinition;
import lombok.Value;

import java.util.Optional;

@Value
public class IntegrationParticipationLinks {

  IntegrationScenarioDefinition integrationScenarioDefinition;
  ConnectorDefinition connectorDefinition;
  Optional<ScenarioParticipationIncomingDefinition> incomingParticipation;
  Optional<ScenarioParticipationOutgoingDefinition> outgoingParticipation;
}
