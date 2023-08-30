package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * Class that exposes the insides of orchestration definition. Those are package private so that
 * they can't be seen on the user side (while the orchestration is written). This class has to stay
 * in the same package as the orchestration definition.
 *
 * <p><em>For internal use only</em>
 */
@UtilityClass
@SuppressWarnings({"rawtypes", "unchecked"})
public class RouteGeneratorHelper {

  public static List<CallableWithinProcessDefinition> getConsumerCalls(
      ProcessOrchestrationDefinition element) {
    return element.getSteps();
  }

  public static Class<? extends IntegrationScenarioDefinition> getConsumerClass(
      CallProcessConsumer element) {
    return element.getConsumerClass();
  }

  public static Optional<CompositeProcessStepRequestExtractor> getRequestPreparation(
      CallProcessConsumer element) {
    return element.getRequestPreparation();
  }

  public static Optional<CompositeProcessStepConditional> getPredicate(Object element) {
    if (element instanceof CallNestedCondition.ProcessBranchStatements ele)
      return Optional.ofNullable(ele.predicate());
    return Optional.empty();
  }

  public static Optional<CompositeProcessStepRequestExtractor> getRequestPreparation(
      CallNestedCondition element) {
    return element.getRequestPreparation();
  }

  public static Optional<CompositeProcessStepResponseConsumer> getResponseConsumer(
      CallProcessConsumer element) {
    return element.getResponseConsumer();
  }

  public static List<CallNestedCondition.ProcessBranchStatements> getConditionalStatements(
      CallNestedCondition element) {
    return element.getConditionalStatements();
  }

  public static List<CallableWithinProcessDefinition> getUnonditionalStatements(
      CallNestedCondition element) {
    return element.getUnconditionalStatements();
  }

  public static Optional<StepResultCloner> getStepResultCloner(CallProcessConsumer element) {
    return element.getStepResultCloner();
  }

  public static Optional<StepResultCloner> getStepResultCloner(CallNestedCondition element) {
    return element.getStepResultCloner();
  }

  public static List<CallableWithinProcessDefinition> getSteps(
      ProcessOrchestrationDefinition orchestrationDef) {
    return orchestrationDef.getSteps();
  }
}
