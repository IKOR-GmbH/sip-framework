package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.OptionalIdentifiedDefinition;

public interface InboundConnectorDefinition<
        DEFINITION_TYPE extends OptionalIdentifiedDefinition<DEFINITION_TYPE>>
    extends ConnectorDefinition, IntegrationScenarioProviderDefinition {

  void defineInboundEndpoints(
      DEFINITION_TYPE definition,
      EndpointProducerBuilder targetToDefinition,
      RoutesRegistry routeRegistry);

  default ConnectorType getConnectorType() {
    return ConnectorType.IN;
  }

  @Override
  default String getScenarioId() {
    return toScenarioId();
  }

  Class<? extends DEFINITION_TYPE> getEndpointDefinitionTypeClass();
}
