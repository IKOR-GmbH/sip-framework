package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;

// TODO: Add javadoc
public interface DeclarationRegistryApi {

  Optional<ConnectorDefinition> getConnectorById(final String connectorId);

  IntegrationScenarioDefinition getScenarioById(final String scenarioId);

  Optional<InboundEndpointDefinition> getInboundEndpointById(String endpointId);

  Optional<OutboundEndpointDefinition> getOutboundEndpointById(String endpointId);

  List<InboundEndpointDefinition> getInboundEndpointsByConnectorId(String connectorId);

  List<OutboundEndpointDefinition> getOutboundEndpointsByConnectorId(String connectorId);

  List<InboundEndpointDefinition> getInboundEndpointsByScenarioId(String scenarioId);

  List<OutboundEndpointDefinition> getOutboundEndpointsByScenarioId(String scenarioId);
}
