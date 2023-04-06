package de.ikor.sip.foundation.core.declarative.orchestration;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Map;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;

public interface ScenarioOrchestrationInfo extends OrchestrationInfo {
  IntegrationScenarioDefinition getIntegrationScenario();

  Map<IntegrationScenarioProviderDefinition, RouteDefinition> getProviderRouteEnds();

  Map<IntegrationScenarioConsumerDefinition, EndpointProducerBuilder> getConsumerStarts();
}
