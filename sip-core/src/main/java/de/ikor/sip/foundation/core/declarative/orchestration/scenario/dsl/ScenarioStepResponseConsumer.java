package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationContext;

@FunctionalInterface
public interface ScenarioStepResponseConsumer<M> {

  void consumeResponse(M latestResponse, ScenarioOrchestrationContext<M> context);
}
