package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.OrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Map;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RoutesDefinition;

public interface ScenarioOrchestrationInfo extends OrchestrationInfo {
  IntegrationScenarioDefinition getIntegrationScenario();

  RoutesDefinition getRoutesDefinition();

  Map<IntegrationScenarioProviderDefinition, EndpointConsumerBuilder>
      getProviderEndpoints();

  Map<IntegrationScenarioConsumerDefinition, EndpointProducerBuilder>
      getConsumerEndpoints();
}
