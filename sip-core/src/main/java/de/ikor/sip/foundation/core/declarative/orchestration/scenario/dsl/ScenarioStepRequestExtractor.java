package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationContext;

@FunctionalInterface
public interface ScenarioStepRequestExtractor<M> {

  Object extractStepRequest(final ScenarioOrchestrationContext<M> context);
}
