package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import org.apache.camel.model.RouteDefinition;

public interface OutboundConnectorDefinition
    extends ConnectorDefinition, IntegrationScenarioConsumerDefinition {
  void defineOutboundEndpoints(RouteDefinition type);

  default ConnectorType getConnectorType() {
    return ConnectorType.OUT;
  }

  @Override
  default String getScenarioId() {
    return fromScenarioId();
  }
}
