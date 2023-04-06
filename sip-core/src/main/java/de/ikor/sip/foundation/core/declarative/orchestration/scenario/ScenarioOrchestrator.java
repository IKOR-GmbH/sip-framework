package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioOrchestrationDefinition;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
public class ScenarioOrchestrator implements Orchestrator<ScenarioOrchestrationInfo> {

  private final Consumer<ScenarioOrchestrationInfo> orchestrationConsumer;
  @Setter private Predicate<ScenarioOrchestrationInfo> canOrchestrate = info -> true;

  public static <M> ScenarioOrchestrator forOrchestrationDsl(
      final Consumer<ScenarioOrchestrationDefinition<M>> dslDefinition) {
    return forOrchestrationConsumer(
        info -> {
          final var orchestrationDef =
              new ScenarioOrchestrationDefinition<M>(info.getIntegrationScenario());
          dslDefinition.accept(orchestrationDef);
        });
  }

  public static ScenarioOrchestrator forOrchestrationConsumer(
      final Consumer<ScenarioOrchestrationInfo> orchestrationConsumer) {
    return new ScenarioOrchestrator(orchestrationConsumer);
  }

  @Override
  public boolean canOrchestrate(final ScenarioOrchestrationInfo data) {
    return canOrchestrate.test(data);
  }

  @Override
  public void doOrchestrate(final ScenarioOrchestrationInfo data) {
    orchestrationConsumer.accept(data);
  }
}
