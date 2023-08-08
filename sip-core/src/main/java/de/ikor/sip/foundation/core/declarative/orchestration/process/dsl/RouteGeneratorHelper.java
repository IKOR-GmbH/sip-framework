package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

/**
 * Class that exposes the insides of orchestration definition. Those are package private so that
 * they can't be seen on the user side (while the orchestration is written). This class has to stay
 * in the same package as the orchestration definition.
 */
@UtilityClass
@SuppressWarnings("rawtypes")
public class RouteGeneratorHelper {

  public static List<ForProcessProviderImpl<?>>
      getScenarioProviderDefinitions(
          ProcessOrchestrationDefinition processOrchestrationDefinition) {
    return processOrchestrationDefinition.getScenarioProviderDefinitions();
  }

  public static Class<? extends IntegrationScenarioDefinition> getProviderClass(
          ForProcessProviderImpl<?> element) {

    return element.getProviderClass();
  }

  public static List<CallProcessConsumerBase> getConsumerCalls(ForProcessProviderImpl element) {
    return element.getSteps();
  }

  public static Class<? extends IntegrationScenarioDefinition> getConsumerClass(
      CallProcessConsumerBase element) {
    return element.getConsumerClass();
  }

  public static Optional<CompositeProcessStepRequestExtractor> getRequestPreparation(
      CallProcessConsumerBase element) {
    return element.getRequestPreparation();
  }

  public static Optional<CompositeProcessStepResponseConsumer> getResponseConsumer(
      CallProcessConsumerBase element) {
    return element.getResponseConsumer();
  }

  public static Optional<CompositeProcessStepConditional> getConditional(
          ForProcessStartCondition element) {
    //TODO
    return element.getConditionals().stream().findFirst();
  }


  public static Optional<StepResultCloner> getStepResultCloner(CallProcessConsumerBase element) {
    return element.getStepResultCloner();
  }
}
