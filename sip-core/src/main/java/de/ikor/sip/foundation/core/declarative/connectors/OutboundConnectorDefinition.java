package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import org.apache.camel.model.OutputDefinition;
import org.apache.camel.model.RouteDefinition;

public interface OutboundConnectorDefinition<ORCHINFO_TYPE extends ConnectorOrchestrationInfo, DEFINITION_TYPE extends OutputDefinition<DEFINITION_TYPE>>
        extends ConnectorDefinition<ORCHINFO_TYPE, DEFINITION_TYPE>,
        IntegrationScenarioConsumerDefinition {
    RouteDefinition defineOutboundEndpoints(DEFINITION_TYPE type);

    default ConnectorType getConnectorType() {
        return ConnectorType.OUT;
    }
}
