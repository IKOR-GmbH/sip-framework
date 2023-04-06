package de.ikor.sip.foundation.core.declarative.orchestration;

import de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario.ScenarioOrchestrationDefinition;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScenarioOrchestratorForDsl implements Orchestrator<ScenarioOrchestrationInfo> {

  private final Consumer<ScenarioOrchestrationDefinition> dslConsumer;

  @Override
  public boolean canOrchestrate(final ScenarioOrchestrationInfo data) {
    return true;
  }

  @Override
  public void doOrchestrate(final ScenarioOrchestrationInfo data) {
    final var orchestrationDef = new ScenarioOrchestrationDefinition();
    dslConsumer.accept(orchestrationDef);
  }
}
