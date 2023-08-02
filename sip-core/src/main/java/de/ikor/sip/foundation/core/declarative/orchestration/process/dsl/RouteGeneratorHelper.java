package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 * Class that exposes the insides of orchestration definition. Those are package private so that
 * they can't be seen on the user side (while the orchestration is written). This class has to stay
 * in the same package as the orchestration definition.
 */
@UtilityClass
@SuppressWarnings("rawtypes")
public class RouteGeneratorHelper {

  public static List<ForProcessProviders<ProcessOrchestrationDefinition>>
      getScenarioProviderDefinitions(
          ProcessOrchestrationDefinition processOrchestrationDefinition) {
    return processOrchestrationDefinition.getScenarioProviderDefinitions();
  }

  public static Set<Class<? extends IntegrationScenarioDefinition>> getProviderClasses(
      ForProcessProviders<ProcessOrchestrationDefinition> element) {
    return element.getProviderClasses();
  }

  public static List<CallProcessConsumer> getConsumerCalls(ForProcessProviders element) {
    return element.getConsumerCalls();
  }

  public static Class<? extends IntegrationScenarioDefinition> getConsumerClass(
      CallProcessConsumer element) {
    return element.getConsumerClass();
  }

  public static Optional<CompositeProcessStepRequestExtractor> getRequestPreparation(
      CallProcessConsumer element) {
    return element.getRequestPreparation();
  }

  public static Optional<CompositeProcessStepResponseConsumer> getResponseConsumer(
      CallProcessConsumer element) {
    return element.getResponseConsumer();
  }

  public static Optional<StepResultCloner> getStepResultCloner(CallProcessConsumer element) {
    return element.getStepResultCloner();
  }
}
