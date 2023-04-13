package de.ikor.sip.foundation.core.declarative.scenario;

/**
 * Interface for consumers to a specific integration scenario.
 *
 * @see IntegrationScenarioDefinition
 * @see de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition
 */
public interface IntegrationScenarioConsumerDefinition {

  /**
   * Returns the unique identifier of the integration scenario that is consumed from.
   *
   * @see IntegrationScenarioDefinition#getId()
   * @return Identifier of the integration scenario
   */
  String fromScenarioId();
}
