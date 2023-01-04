package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Map;

public interface ConnectorDefinition {

  String getID();

  String getDocumentation();

  Map<String, IntegrationScenarioConsumerDefinition> getConsumedIntegrationScenarios();

  Map<String, IntegrationScenarioProviderDefinition> getProvidedIntegrationScenarios();
}
