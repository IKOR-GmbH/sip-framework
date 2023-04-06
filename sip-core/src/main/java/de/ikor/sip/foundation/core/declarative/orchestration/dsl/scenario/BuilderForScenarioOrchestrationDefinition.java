package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.orchestration.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class BuilderForScenarioOrchestrationDefinition<M> implements Runnable {

  private final ScenarioOrchestrationDefinition<M> scenarioOrchestrationDefinition;
  private final ScenarioOrchestrationInfo orchestrationInfo;
  private final DeclarationsRegistryApi declarationsRegistry;

  @Override
  public void run() {
    final var scenarioProviders = orchestrationInfo.getProviderRouteEnds().keySet();
    final var unhandledProviders = new HashSet<>(scenarioProviders);

    for (ForScenarioProvidersBaseDefinition scenarioProviderDefinition :
        scenarioOrchestrationDefinition.getScenarioProviderDefinitions()) {
      final Set<IntegrationScenarioProviderDefinition> handledProviders = new HashSet<>();
      if (scenarioProviderDefinition instanceof ForScenarioProvidersWithClassDefinition element) {
        handledProviders.addAll(resolveProviders(element));
      } else if (scenarioProviderDefinition
          instanceof ForScenarioProvidersWithConnectorIdDefinition element) {
        handledProviders.addAll(resolveProviders(element));
      } else if (scenarioProviderDefinition
          instanceof ForScenarioProvidersCatchAllDefinition element) {
        handledProviders.addAll(unhandledProviders);
      } else {
        throw new SIPFrameworkInitializationException();
      }
    }
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProviders(
      final ForScenarioProvidersWithClassDefinition element) {
    return declarationsRegistry
        .getInboundConnectorsByScenarioId(orchestrationInfo.getIntegrationScenario().getId())
        .stream()
        .filter(element.getProviderClasses()::contains)
        .collect(Collectors.toSet());
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProviders(
      final ForScenarioProvidersWithConnectorIdDefinition element) {
    return declarationsRegistry
        .getInboundConnectorsByScenarioId(orchestrationInfo.getIntegrationScenario().getId())
        .stream()
        .filter(element.getConnectorIds()::contains)
        .collect(Collectors.toSet());
  }
}
