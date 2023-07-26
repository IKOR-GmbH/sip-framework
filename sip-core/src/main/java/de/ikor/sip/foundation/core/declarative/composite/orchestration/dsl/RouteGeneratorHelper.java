package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.orchestration.CompositeScenarioStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.composite.orchestration.CompositeScenarioStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * Class that exposes the insides of orchestration definition. Those are package private so that
 * they can't be seen on the user side (while the orchestration is written). This class has to stay
 * in the same package as the orchestration definition.
 */
@RequiredArgsConstructor
public class RouteGeneratorHelper {

  public static List<ForProcessProviders> getScenarioProviderDefinitions(
      ProcessOrchestrationDefinition processOrchestrationDefinition) {
    return processOrchestrationDefinition.getScenarioProviderDefinitions();
  }

  public static Set<Class<? extends IntegrationScenarioDefinition>> getProviderClasses(
      ForProcessProviders element) {
    return element.getProviderClasses();
  }

  public static List<ProcessCallableWithinProviderDefinition> getNodes(
      ForProcessProviders element) {
    return element.getConsumerCalls();
  }

  public static Class<? extends IntegrationScenarioDefinition> getConsumerClass(
      CallProcessConsumer element) {
    return element.getConsumerClass();
  }

  public static <M> Optional<CompositeScenarioStepRequestExtractor<M>> getRequestPreparation(
      CallProcessConsumer element) {
    return element.getRequestPreparation();
  }

  public static <M> Optional<CompositeScenarioStepResponseConsumer<M>> getResponseConsumer(
      CallProcessConsumer element) {
    return element.getResponseConsumer();
  }

  public static <M> Optional<StepResultCloner<M>> getStepResultCloner(CallProcessConsumer element) {
    return element.getStepResultCloner();
  }
}
