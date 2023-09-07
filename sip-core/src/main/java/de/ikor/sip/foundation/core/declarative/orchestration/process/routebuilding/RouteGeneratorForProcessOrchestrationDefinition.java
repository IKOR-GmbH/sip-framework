package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ProcessOrchestrationDefinition;
import java.util.ArrayList;
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

  private final ProcessOrchestrationDefinition processOrchestrationDefinition;

  public RouteGeneratorForProcessOrchestrationDefinition(
      final CompositeProcessOrchestrationInfo orchestrationInfo,
      final ProcessOrchestrationDefinition processOrchestrationDefinition) {
    super(orchestrationInfo);
    this.processOrchestrationDefinition = processOrchestrationDefinition;
  }

  @Override
  public void run() {
    final var scenarioProvidersOverall = getOrchestrationInfo().getProviderEndpoints().keySet();
    final var unhandledProvidersOverall = new HashSet<>(scenarioProvidersOverall);
    final List<RouteGeneratorForProcessProviders> providerBuilders = new ArrayList<>();

    final var builder =
        new RouteGeneratorForProcessProviders(
            getOrchestrationInfo(), processOrchestrationDefinition);
    unhandledProvidersOverall.remove(builder.getHandledProvider());
    providerBuilders.add(builder);
    builder.generateRoutes(getRoutesDefinition());
  }
}
