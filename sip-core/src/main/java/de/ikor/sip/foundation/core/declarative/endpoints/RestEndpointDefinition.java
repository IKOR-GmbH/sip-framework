package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.Bridgeable;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import org.apache.camel.builder.EndpointConsumerBuilder;

public interface RestEndpointDefinition
    extends IntegrationScenarioProviderDefinition, Orchestratable<EndpointOrchestrationInfo>, Bridgeable {

  ConnectorDefinition getConnector();
}
