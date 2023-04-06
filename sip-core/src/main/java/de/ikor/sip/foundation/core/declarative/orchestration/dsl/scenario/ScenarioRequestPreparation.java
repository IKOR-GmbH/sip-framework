package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

@FunctionalInterface
public interface ScenarioRequestPreparation<T> {

  T getPreparedRequest(final ScenarioOrchestrationContext context);
}
