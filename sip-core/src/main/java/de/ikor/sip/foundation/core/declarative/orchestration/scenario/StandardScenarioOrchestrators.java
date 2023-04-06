package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StandardScenarioOrchestrators
    implements Supplier<Orchestrator<ScenarioOrchestrationInfo>> {
  ANY_TO_ONE(
      () ->
          ScenarioOrchestrator.forOrchestrationDsl(
                  dsl -> dsl.forAnyUnspecifiedProvider().callAnyUnspecifiedConsumer())
              .setCanOrchestrate(info -> info.getConsumerEndpoints().size() == 1)),
  ANY_TO_ANY_WITHOUT_RESPONSE(
      () ->
          ScenarioOrchestrator.forOrchestrationDsl(
                  dsl -> dsl.forAnyUnspecifiedProvider().callAnyUnspecifiedConsumer())
              .setCanOrchestrate(info -> !info.getIntegrationScenario().hasResponseFlow()));

  private final Supplier<Orchestrator<ScenarioOrchestrationInfo>> delegate;

  @Override
  public Orchestrator<ScenarioOrchestrationInfo> get() {
    return delegate.get();
  }
}
