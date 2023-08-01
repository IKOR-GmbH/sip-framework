package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ProcessOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding.RouteGeneratorForProcessOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrator meant to be attached to {@link
 * de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess}
 *
 * <p>Orchestration can be defined in orchestration DSL via {@link #forOrchestrationDsl(Consumer)}.
 *
 * @see CompositeProcessDefinition#getOrchestrator()
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeOrchestrator implements Orchestrator<CompositeOrchestrationInfo> {

  private final Consumer<CompositeOrchestrationInfo> orchestrationInfoConsumer;
  @Setter private Predicate<CompositeOrchestrationInfo> canOrchestrate = Objects::nonNull;

  /**
   * Creates a new orchestrator specified via orchestration-DSL
   *
   * @param dslDefinition Consumer that specifies the orchestration via DSL
   * @return Orchestrator as specified in the DSL
   */
  @SuppressWarnings("java:S1172")
  public static <T> CompositeOrchestrator forOrchestrationDsl(
      final Consumer<ProcessOrchestrationDefinition> dslDefinition) {
    return forOrchestrationConsumer(
        orchestrationInfo -> {
          final var orchestrationDef =
              new ProcessOrchestrationDefinition(orchestrationInfo.getCompositeProcess());
          dslDefinition.accept(orchestrationDef);
          new RouteGeneratorForProcessOrchestrationDefinition(orchestrationInfo, orchestrationDef)
              .run();
        });
  }

  /**
   * Creates a new orchestrator specified via a consumer for the {@link ScenarioOrchestrationInfo}.
   *
   * <p>This is very low-level, and it is strongly recommended to define the orchestration via DSL
   * instead. This can be used to manually create routes between endpoints.
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
    orchestrationInfoConsumer.accept(info);
  }
}
