package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;

public interface InEndpointDefinition
    extends IntegrationScenarioProviderDefinition, Orchestratable<EndpointOrchestrationInfo> {

  ConnectorDefinition getConnector();
}
