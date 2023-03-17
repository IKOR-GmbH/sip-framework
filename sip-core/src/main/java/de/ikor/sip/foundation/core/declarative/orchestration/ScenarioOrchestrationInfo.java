package de.ikor.sip.foundation.core.declarative.orchestration;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import java.util.Map;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;

public interface ScenarioOrchestrationInfo extends OrchestrationInfo {

  Map<InboundConnectorDefinition, RouteDefinition> getInboundConnectorRouteEnds();

  Map<OutboundConnectorDefinition, EndpointProducerBuilder> getOutboundConnectorsStarts();
}
