package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

/** DSL class for calling all remaining scenario consumers */
public class CallScenarioConsumerCatchAllDefinition<R, M>
    extends CallScenarioConsumerBaseDefinition<CallScenarioConsumerCatchAllDefinition<R, M>, R, M> {

  CallScenarioConsumerCatchAllDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }
}
