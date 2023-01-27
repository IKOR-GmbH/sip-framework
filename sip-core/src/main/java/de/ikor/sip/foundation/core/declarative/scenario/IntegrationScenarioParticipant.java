package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;

/**
 * Defines common methods for {@link IntegrationScenarioConsumerDefinition} and {@link
 * IntegrationScenarioProviderDefinition}
 */
public interface IntegrationScenarioParticipant {

  ConnectorDefinition getConnector();

  String getConnectorId();
}
