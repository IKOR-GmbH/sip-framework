package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import org.apache.camel.builder.EndpointProducerBuilder;

public interface OutboundEndpointDefinition
    extends EndpointDefinition,
        IntegrationScenarioConsumerDefinition,
        Orchestratable<EndpointOrchestrationInfo> {

  EndpointProducerBuilder getOutboundEndpoint();

  default EndpointType getEndpointType() {
    return EndpointType.OUT;
  }
}
