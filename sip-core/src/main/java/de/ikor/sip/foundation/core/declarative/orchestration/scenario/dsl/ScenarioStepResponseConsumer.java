package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

@FunctionalInterface
public interface ScenarioStepResponseConsumer<M> {

  void consumeResponse(M latestResponse, ScenarioOrchestrationContext context);
}
