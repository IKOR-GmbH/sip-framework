package de.ikor.sip.foundation.core.declarative.scenario;

/**
 * Interface for providers of a specific integration scenario.
 *
 * @see IntegrationScenarioDefinition
 * @see de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition
 */
public interface IntegrationScenarioProviderDefinition {

  /**
   * Returns the unique identifier of the integration scenario that is provided to.
   *
   * @see IntegrationScenarioDefinition#getId()
   * @return Identifier of the integration scenario
   */
  String toScenarioId();
}
