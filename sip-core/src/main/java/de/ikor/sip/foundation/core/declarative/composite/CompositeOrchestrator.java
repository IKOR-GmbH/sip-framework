package de.ikor.sip.foundation.core.declarative.composite;

import de.ikor.sip.foundation.core.declarative.composite.orchestration.CompositeScenarioOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.composite.orchestration.RouteGeneratorForCompositeScenarioOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.StandardScenarioOrchestrators;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrator meant to be attached to integration-scenarios.
 *
 * <p>Default orchestrators can be found in {@link StandardScenarioOrchestrators}, while custom ones
 * can be defined in orchestration DSL via {@link #forOrchestrationDslWithoutResponse(Consumer)} or
 * {@link #forOrchestrationDslWithResponse(Class, Consumer)}.
 *
 * @see IntegrationScenarioBase#getOrchestrator()
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
public class CompositeOrchestrator implements Orchestrator<CompositeOrchestrationInfo> {

  private final Consumer<CompositeOrchestrationInfo> orchestrationConsumer;
  @Setter private Predicate<CompositeOrchestrationInfo> canOrchestrate = Objects::nonNull;

  /**
   * Creates a new orchestrator specified via orchestration-DSL for scenarios that do not
   * require/specify a response model.
   *
   * @param dslDefinition Consumer that specifies the orchestration via DSL
   * @return Orchestrator as specified in the DSL
   */
  public static CompositeOrchestrator forOrchestrationDslWithoutResponse(
      final Consumer<CompositeScenarioOrchestrationDefinition<Void>> dslDefinition) {
    return forOrchestrationDslWithResponse(Void.class, dslDefinition);
  }

  /**
   * Creates a new orchestrator specified via orchestration-DSL for scenarios that require/specify a
   * response model.
   *
   * @param responseType The response type as defined in the integration scenario that should be
   *     orchestrated
   * @param dslDefinition Consumer that specifies the orchestration via DSL
   * @return Orchestrator as specified in the DSL
   * @param <T> Response model type
   */
  @SuppressWarnings("java:S1172")
  public static <T> CompositeOrchestrator forOrchestrationDslWithResponse(
      final Class<T> responseType,
      final Consumer<CompositeScenarioOrchestrationDefinition<T>> dslDefinition) {
    return forOrchestrationConsumer(
        info -> {
          final var orchestrationDef =
              new CompositeScenarioOrchestrationDefinition<T>(info.getCompositeProcess());
          dslDefinition.accept(orchestrationDef);
          new RouteGeneratorForCompositeScenarioOrchestrationDefinition<>(info, orchestrationDef)
              .run();
        });
  }

  /**
   * Creates a new orchestrator specified via a consumer for the {@link ScenarioOrchestrationInfo}.
   *
   * <p>This is very low-level, and it is strongly recommended to define the orchestration via DSL
   * instead.
   *
   * @param orchestrationConsumer Consumer for the orchestration-info provided by the framework
   * @return Orchestrator
   */
  public static CompositeOrchestrator forOrchestrationConsumer(
      final Consumer<CompositeOrchestrationInfo> orchestrationConsumer) {
    return new CompositeOrchestrator(orchestrationConsumer);
  }

  @Override
  public boolean canOrchestrate(final CompositeOrchestrationInfo info) {
    return canOrchestrate.test(info);
  }

  @Override
  public void doOrchestrate(final CompositeOrchestrationInfo info) {
    orchestrationConsumer.accept(info);
  }
}
