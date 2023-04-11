package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("rawtypes")
public class RouteGeneratorForScenarioOrchestrationDefinition<M> extends RouteGeneratorBase
    implements Runnable {

  private final ScenarioOrchestrationDefinition<M> scenarioOrchestrationDefinition;

  public RouteGeneratorForScenarioOrchestrationDefinition(
      final ScenarioOrchestrationInfo orchestrationInfo,
      final ScenarioOrchestrationDefinition<M> scenarioOrchestrationDefinition) {
    super(orchestrationInfo);
    this.scenarioOrchestrationDefinition = scenarioOrchestrationDefinition;
  }

  @Override
  public void run() {
    final var scenarioProvidersOverall = getOrchestrationInfo().getProviderEndpoints().keySet();
    final var unhandledProvidersOverall = new HashSet<>(scenarioProvidersOverall);
    final List<RouteGeneratorForScenarioProvidersDefinition> providerBuilders = new ArrayList<>();

    for (ForScenarioProvidersBaseDefinition providerDefinition :
        scenarioOrchestrationDefinition.getScenarioProviderDefinitions()) {

      final var builder =
          new RouteGeneratorForScenarioProvidersDefinition(
              getOrchestrationInfo(),
              providerDefinition,
              Collections.unmodifiableSet(unhandledProvidersOverall));

      if (builder.getHandledProviders().isEmpty()) {
        continue;
      }
      unhandledProvidersOverall.removeAll(builder.getHandledProviders());
      providerBuilders.add(builder);
    }

    providerBuilders.forEach(builder -> builder.generateRoutes(getRoutesDefinition()));
  }
}
