package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;

// TODO: Add javadoc
public interface DeclarationRegistryApi {

  Optional<ConnectorGroupDefinition> getConnectorGroupById(final String connectorGroupId);

  IntegrationScenarioDefinition getScenarioById(final String scenarioId);

  Optional<ConnectorDefinition> getConnectorById(final String connectorId);

  List<InboundConnectorDefinition> getInboundConnectors();

  List<OutboundConnectorDefinition> getOutboundConnectors();

  Optional<InboundConnectorDefinition> getInboundConnectorById(String connectorId);

  Optional<OutboundConnectorDefinition> getOutboundConnectorById(String connectorId);

  List<InboundConnectorDefinition> getInboundConnectorsByConnectorGroupId(String connectorGroupId);

  List<OutboundConnectorDefinition> getOutboundEndpointsByConnectorGroupId(String connectorGroupId);

  List<InboundConnectorDefinition> getInboundConnectorsByScenarioId(String scenarioId);

  List<OutboundConnectorDefinition> getOutboundConnectorsByScenarioId(String scenarioId);
}
