package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.dto.ProcessOrchestrationDefinitionDto;
import de.ikor.sip.foundation.core.declarative.dto.StepDto;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.*;
import de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding.RouteGeneratorForProcessOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  private Optional<Consumer<ProcessOrchestrationDefinition>> dslDefinition = Optional.empty();

  private CompositeOrchestrator(
      Consumer<CompositeOrchestrationInfo> orchestrationInfoConsumer,
      Consumer<ProcessOrchestrationDefinition> dslDefinition) {
    this.orchestrationInfoConsumer = orchestrationInfoConsumer;
    this.dslDefinition = Optional.of(dslDefinition);
  }
  /**
   * Creates a new orchestrator specified via orchestration-DSL
   *
   * @param dslDefinition Consumer that specifies the orchestration via DSL
   * @return Orchestrator as specified in the DSL
   */
  @SuppressWarnings("java:S1172")
  public static CompositeOrchestrator forOrchestrationDsl(
      final Consumer<ProcessOrchestrationDefinition> dslDefinition) {
    return new CompositeOrchestrator(
        orchestrationInfo -> {
          final var orchestrationDef =
              new ProcessOrchestrationDefinition(orchestrationInfo.getCompositeProcess());
          dslDefinition.accept(orchestrationDef);
          new RouteGeneratorForProcessOrchestrationDefinition(orchestrationInfo, orchestrationDef)
              .run();
        },
        dslDefinition);
  }

  /**
   * Creates a new orchestrator specified via a consumer for the {@link CompositeOrchestrationInfo}.
   *
   * <p>This is very low-level, and it is strongly recommended to define the orchestration via DSL
   * instead. This can be used to manually create routes between endpoints.
   *
   * @param orchestrationInfoConsumer Consumer for the orchestration-info provided by the framework
   * @return Orchestrator
   */
  public static CompositeOrchestrator forOrchestrationConsumer(
      final Consumer<CompositeOrchestrationInfo> orchestrationInfoConsumer) {
    return new CompositeOrchestrator(orchestrationInfoConsumer);
  }

  /**
   * Provides a way to get the definition that is created inside the DSL.
   *
   * @param compositeProcessDefinition definition of a composite process
   * @return orchestration definition
   */
  public ProcessOrchestrationDefinitionDto populateOrchestrationDefinition(
      CompositeProcessDefinition compositeProcessDefinition) {
    if (dslDefinition.isPresent()) {
      final var orchestrationDef = new ProcessOrchestrationDefinition(compositeProcessDefinition);
      dslDefinition.get().accept(orchestrationDef);
      ProcessOrchestrationDefinitionDto processOrchestrationDefinitionDto =
              ProcessOrchestrationDefinitionDto.builder()
                      //.conditions(getConditions(orchestrationDef))
                      .steps(getSteps(orchestrationDef))
                      .build();
      return processOrchestrationDefinitionDto;
    }
    return null;
  }

  private List<StepDto> getSteps(ProcessOrchestrationDefinition orchestrationDef) {
    return RouteGeneratorHelper.getSteps(orchestrationDef).stream().flatMap(this::getStep).collect(Collectors.toList());
  }

  private Stream<StepDto> getStep(CallableWithinProcessDefinition callableWithinProcessDefinition) {
    List<StepDto> steps = new ArrayList<>();
    if(callableWithinProcessDefinition instanceof CallNestedCondition<?> nestedCondition) {
      var conditionalStatements = RouteGeneratorHelper.getConditionalStatements(nestedCondition);
      var unconditionalStatements = RouteGeneratorHelper.getUnonditionalStatements(nestedCondition);
      List<StepDto> nestedStepsCond = new ArrayList<>();
      List<StepDto> nestedStepsUn = new ArrayList<>();
      conditionalStatements.forEach(statement -> {
        statement.statements().forEach(inner -> nestedStepsCond.add(extracted(inner, true)));
      });
      unconditionalStatements.forEach(statement -> {
        nestedStepsUn.add(extracted(statement, true));
      });
      steps.add(StepDto.builder()
              .conditioned(true)
              .nested(nestedStepsCond)
              .build());
      steps.add(StepDto.builder()
              .conditioned(true)
              .nested(nestedStepsUn)
              .build());
    }
    if(callableWithinProcessDefinition instanceof CallProcessConsumerImpl<?> unconditionedStep){
      steps.add(extracted(callableWithinProcessDefinition, false));
    }
    return steps.stream();
  }

  private StepDto extracted(CallableWithinProcessDefinition statement, boolean conditioned) {
    if(statement instanceof CallProcessConsumerImpl<?> impl) {
      String consumer = RouteGeneratorHelper.getConsumerClass(impl).getAnnotation(IntegrationScenario.class).scenarioId();
      return StepDto.builder()
              .conditioned(conditioned)
              .consumerId(consumer)
              .requestPreparation(RouteGeneratorHelper.getRequestPreparation(impl).isPresent())
              .responseHandling(RouteGeneratorHelper.getResponseConsumer(impl).isPresent())
              .build();
    }
    return null;
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
