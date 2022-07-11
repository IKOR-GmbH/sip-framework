package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.definitions.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationIncomingDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.ScenarioParticipationOutgoingDefinition;
import java.util.Optional;
import lombok.Value;

@Value
public class IntegrationParticipationLinks {

  IntegrationScenarioDefinition integrationScenarioDefinition;
  ConnectorDefinition connectorDefinition;
  Optional<ScenarioParticipationIncomingDefinition> incomingParticipation;
  Optional<ScenarioParticipationOutgoingDefinition> outgoingParticipation;
}
