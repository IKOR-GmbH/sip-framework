package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import org.apache.camel.builder.EndpointConsumerBuilder;

public interface InboundEndpointDefinition
    extends IntegrationScenarioProviderDefinition, Orchestratable<EndpointOrchestrationInfo>, EndpointWithAfter{
  EndpointConsumerBuilder getInboundEndpoint();
}
