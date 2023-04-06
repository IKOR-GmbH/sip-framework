package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

@FunctionalInterface
public interface ScenarioResponseAggregator<M> {

  M mapResponseInOverallResponse(final M ScenarioOrchestrationContext);
}
