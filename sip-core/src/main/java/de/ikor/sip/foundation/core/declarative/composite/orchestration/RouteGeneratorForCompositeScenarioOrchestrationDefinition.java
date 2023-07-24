package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeOrchestrationInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Class for generating Camel routes for scenario orchestration via DSL
 *
 * <p><em>For internal use only</em>
 */
@SuppressWarnings("rawtypes")
public final class RouteGeneratorForCompositeScenarioOrchestrationDefinition<M>
    extends RouteGeneratorCompositeBase implements Runnable {

  private final CompositeScenarioOrchestrationDefinition<M> scenarioOrchestrationDefinition;

  public RouteGeneratorForCompositeScenarioOrchestrationDefinition(
      final CompositeOrchestrationInfo orchestrationInfo,
      final CompositeScenarioOrchestrationDefinition<M> scenarioOrchestrationDefinition) {
    super(orchestrationInfo);
    this.scenarioOrchestrationDefinition = scenarioOrchestrationDefinition;
  }

  @Override
  public void run() {
    final var scenarioProvidersOverall = getOrchestrationInfo().getProviderEndpoints().keySet();
    final var unhandledProvidersOverall = new HashSet<>(scenarioProvidersOverall);
    final List<RouteGeneratorForCompositeScenarioProvidersDefinition> providerBuilders =
        new ArrayList<>();

    for (ForCompositeScenarioProvidersBaseDefinition providerDefinition :
        scenarioOrchestrationDefinition.getScenarioProviderDefinitions()) {

      final var builder =
          new RouteGeneratorForCompositeScenarioProvidersDefinition<M>(
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
