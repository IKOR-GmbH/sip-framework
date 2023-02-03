package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import org.apache.camel.builder.EndpointProducerBuilder;

public interface OutboundEndpointDefinition
    extends IntegrationScenarioConsumerDefinition, Orchestratable<EndpointOrchestrationInfo> {

  String getAnnotationEndpointId();

  EndpointProducerBuilder getOutboundEndpoint();
}
