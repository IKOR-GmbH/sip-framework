package de.ikor.sip.foundation.core.declarative.scenario;


/**
 * Defines common methods for {@link IntegrationScenarioConsumerDefinition} and {@link
 * IntegrationScenarioProviderDefinition}
 */
public interface IntegrationScenarioParticipant {

  //  ConnectorDefinition getConnector();

  String getConnectorId();

  String getScenarioId();
}
