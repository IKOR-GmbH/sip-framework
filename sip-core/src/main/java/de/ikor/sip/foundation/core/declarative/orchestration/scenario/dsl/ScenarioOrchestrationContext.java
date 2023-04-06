package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.OrchestrationContext;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

public class ScenarioOrchestrationContext
    extends OrchestrationContext<
        IntegrationScenarioDefinition, IntegrationScenarioConsumerDefinition> {

  public <R> ScenarioOrchestrationContext(
      final IntegrationScenarioDefinition integrationScenario, final R originalRequest) {
    super(integrationScenario, originalRequest);
  }

  public IntegrationScenarioDefinition getIntegrationScenario() {
    return getOrchestratedElement();
  }
}
