package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

@FunctionalInterface
public interface ScenarioRequestPreparation<M> {

  M getPreparedRequest(final ScenarioOrchestrationContext context);
}
