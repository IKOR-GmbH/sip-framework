package de.ikor.sip.foundation.core.declarative.orchestration;

import java.util.List;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;

public interface ScenarioOrchestrationInfo extends OrchestrationInfo {

  List<RouteDefinition> getInboundConnectorRouteEnds();

  List<? extends EndpointProducerBuilder> getOutboundConnectorsStarts();
}
