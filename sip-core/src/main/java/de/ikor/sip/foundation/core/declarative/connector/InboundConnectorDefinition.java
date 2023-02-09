package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public interface InboundConnectorDefinition<ORCHINFO_TYPE extends ConnectorOrchestrationInfo, DEFINITION_TYPE extends OptionalIdentifiedDefinition<DEFINITION_TYPE>>
        extends ConnectorDefinition<ORCHINFO_TYPE, DEFINITION_TYPE>,
        IntegrationScenarioProviderDefinition {

    List<RouteDefinition> defineInboundEndpoints(DEFINITION_TYPE definition);

    default ConnectorType getConnectorType() {
        return ConnectorType.IN;
    }

}
