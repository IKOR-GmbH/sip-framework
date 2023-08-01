package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ForProcessProviders;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ProcessOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.RouteGeneratorHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Class for generating Camel routes for process orchestration via DSL
 *
 * <p><em>For internal use only</em>
 */
@SuppressWarnings("rawtypes")
public final class RouteGeneratorForProcessOrchestrationDefinition extends RouteGeneratorProcessBase
    implements Runnable {

  private final ProcessOrchestrationDefinition scenarioOrchestrationDefinition;

  public RouteGeneratorForProcessOrchestrationDefinition(
      final CompositeOrchestrationInfo orchestrationInfo,
      final ProcessOrchestrationDefinition scenarioOrchestrationDefinition) {
    super(orchestrationInfo);
    this.scenarioOrchestrationDefinition = scenarioOrchestrationDefinition;
  }

  @Override
  public void run() {
    final var scenarioProvidersOverall = getOrchestrationInfo().getProviderEndpoints().keySet();
    final var unhandledProvidersOverall = new HashSet<>(scenarioProvidersOverall);
    final List<RouteGeneratorForProcessProviders> providerBuilders = new ArrayList<>();

    for (ForProcessProviders providerDefinition :
        RouteGeneratorHelper.getScenarioProviderDefinitions(scenarioOrchestrationDefinition)) {

      final var builder =
          new RouteGeneratorForProcessProviders(
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
