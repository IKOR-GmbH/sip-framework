package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

@FunctionalInterface
public interface ScenarioStepRequestExtractor<M> {

  M extractStepRequest(final ScenarioOrchestrationContext context);
}
