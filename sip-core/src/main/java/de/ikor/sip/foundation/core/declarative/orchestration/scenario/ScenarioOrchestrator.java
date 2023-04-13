package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.RouteGeneratorForScenarioOrchestrationDefinition;
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

  public static ScenarioOrchestrator forOrchestrationDslWithoutResponse(
      final Consumer<ScenarioOrchestrationDefinition<Void>> dslDefinition) {
    return forOrchestrationDslWithResponse(Void.class, dslDefinition);
  }

  @SuppressWarnings("java:S1172")
  public static <T> ScenarioOrchestrator forOrchestrationDslWithResponse(
      final Class<T> responseType,
      final Consumer<ScenarioOrchestrationDefinition<T>> dslDefinition) {
    return forOrchestrationConsumer(
        info -> {
          final var orchestrationDef =
              new ScenarioOrchestrationDefinition<T>(info.getIntegrationScenario());
          dslDefinition.accept(orchestrationDef);
          new RouteGeneratorForScenarioOrchestrationDefinition<>(info, orchestrationDef).run();
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
