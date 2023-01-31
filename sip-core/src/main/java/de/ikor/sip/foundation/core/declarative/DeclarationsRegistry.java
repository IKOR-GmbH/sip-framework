package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedInboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedOutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class DeclarationsRegistry {

  private List<ConnectorDefinition> connectors;
  private List<IntegrationScenarioDefinition> integrationScenarios;
  private List<InboundEndpointDefinition> inboundEndpoints;
  private List<OutboundEndpointDefinition> outboundEndpoints;

  // TODO some check for unique id-s
  @PostConstruct
  private void updateEndpoints() {
    inboundEndpoints.forEach(
        endpoint -> ((AnnotatedInboundEndpoint) endpoint).setDeclarationsRegistry(this));
    outboundEndpoints.forEach(
        endpoint -> ((AnnotatedOutboundEndpoint) endpoint).setDeclarationsRegistry(this));
  }

  public ConnectorDefinition getConnectorById(final String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getID().equals(connectorId))
        .findFirst()
        .orElse(null); // TODO
  }

  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return integrationScenarios.stream()
        .filter(scenario -> scenario.getID().equals(scenarioId))
        .findFirst()
        .orElse(null); // TODO
  }
}
