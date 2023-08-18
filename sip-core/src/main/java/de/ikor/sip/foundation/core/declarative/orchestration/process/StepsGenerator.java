package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.dto.StepDto;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StepsGenerator {
  private int stepOrder = 0;
  private final ProcessOrchestrationDefinition orchestrationDef;

  public List<StepDto> generateSteps() {
    return RouteGeneratorHelper.getSteps(orchestrationDef).stream()
        .flatMap(this::createStepsFromDefinition)
        .toList();
  }

  private Stream<StepDto> createStepsFromDefinition(
      CallableWithinProcessDefinition callableWithinProcessDefinition) {
    List<StepDto> steps = new ArrayList<>();
    if (callableWithinProcessDefinition instanceof CallNestedCondition<?> nestedCondition) {
      fillConditionalSteps(steps, RouteGeneratorHelper.getConditionalStatements(nestedCondition));
      fillUnconditionalSteps(
          steps, RouteGeneratorHelper.getUnonditionalStatements(nestedCondition));
    }
    if (callableWithinProcessDefinition instanceof CallProcessConsumerImpl<?>) {
      steps.add(createBaseStep(callableWithinProcessDefinition));
    }
    return steps.stream();
  }

  private void fillConditionalSteps(
      List<StepDto> steps,
      List<CallNestedCondition.ProcessBranchStatements> conditionalStatements) {
    conditionalStatements.forEach(
        conditionalStatement -> {
          fillUnconditionalSteps(steps, conditionalStatement.statements());
        });
  }

  private void fillUnconditionalSteps(
      List<StepDto> steps, List<CallableWithinProcessDefinition> unconditionalStatements) {
    List<StepDto> unconditionalNestedSteps = new ArrayList<>();
    unconditionalStatements.forEach(
        statement -> unconditionalNestedSteps.add(createBaseStep(statement)));
    steps.add(StepDto.builder().conditioned(true).nested(unconditionalNestedSteps).build());
  }

  private StepDto createBaseStep(CallableWithinProcessDefinition statement) {
    if (statement instanceof CallProcessConsumerImpl<?> impl) {
      String consumer =
          RouteGeneratorHelper.getConsumerClass(impl)
              .getAnnotation(IntegrationScenario.class)
              .scenarioId();
      return StepDto.builder()
          .stepOrder(stepOrder++)
          .conditioned(false)
          .consumerId(consumer)
          .requestPreparation(RouteGeneratorHelper.getRequestPreparation(impl).isPresent())
          .responseHandling(RouteGeneratorHelper.getResponseConsumer(impl).isPresent())
          .build();
    }
    return null;
  }
}
