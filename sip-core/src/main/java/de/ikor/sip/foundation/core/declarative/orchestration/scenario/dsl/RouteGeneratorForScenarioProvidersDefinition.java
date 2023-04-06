package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@SuppressWarnings("rawtypes")
public class RouteGeneratorForScenarioProvidersDefinition extends RouteGeneratorBase {

  private final ForScenarioProvidersBaseDefinition providerDefinition;
  private final Set<IntegrationScenarioProviderDefinition> overallUnhandledProviders;
  private final Set<IntegrationScenarioProviderDefinition> scenarioProviders;

  @Getter(lazy = true)
  private final Set<IntegrationScenarioProviderDefinition> handledProviders =
      resolveAndVerifyHandledProviders();

  RouteGeneratorForScenarioProvidersDefinition(
      final ScenarioOrchestrationInfo orchestrationInfo,
      final ForScenarioProvidersBaseDefinition providerDefinition,
      final Set<IntegrationScenarioProviderDefinition> overallUnhandledProviders,
      final Set<IntegrationScenarioProviderDefinition> scenarioProviders) {
    super(orchestrationInfo);
    this.providerDefinition = providerDefinition;
    this.overallUnhandledProviders = overallUnhandledProviders;
    this.scenarioProviders = scenarioProviders;
  }

  private Set<IntegrationScenarioProviderDefinition> resolveAndVerifyHandledProviders() {
    final var providers = resolveHandledProviders();

    // verify that given providers are registered with scenario
    final var unregisteredProviders =
        providers.stream().filter(handled -> !scenarioProviders.contains(handled)).toList();
    if (!unregisteredProviders.isEmpty()) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "The following providers are used in orchestration for scenario '%s', but not registered with that scenario: %s",
              getOrchestrationInfo().getIntegrationScenario().getId(),
              unregisteredProviders.stream()
                  .map(obj -> obj.getClass().getName())
                  .collect(Collectors.joining(","))));
    }

    // verify that given providers are not already handled
    final var doubleHandledProviders =
        providers.stream().filter(handled -> !overallUnhandledProviders.contains(handled)).toList();
    if (!doubleHandledProviders.isEmpty()) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "The following providers are used more than once in orchestration for scenario '%s': %s",
              getOrchestrationInfo().getIntegrationScenario().getId(),
              doubleHandledProviders.stream()
                  .map(obj -> obj.getClass().getName())
                  .collect(Collectors.joining(","))));
    }

    return providers;
  }

  private Set<IntegrationScenarioProviderDefinition> resolveHandledProviders() {
    if (providerDefinition instanceof ForScenarioProvidersWithClassDefinition element) {
      return resolveProvidersFromClasses(element);
    } else if (providerDefinition
        instanceof ForScenarioProvidersWithConnectorIdDefinition element) {
      return resolveProvidersFromConnectorIds(element);
    } else if (providerDefinition instanceof ForScenarioProvidersCatchAllDefinition) {
      return overallUnhandledProviders;
    } else {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Unhandled scenario-provider sublcass: %s", providerDefinition.getClass().getName()));
    }
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProvidersFromClasses(
      final ForScenarioProvidersWithClassDefinition element) {
    return getDeclarationsRegistry()
        .getInboundConnectorsByScenarioId(
            getOrchestrationInfo()
                .getIntegrationScenario()
                .getId()) // TODO: map against provider-based list from registry, once available
        .stream()
        .filter(element.getProviderClasses()::contains)
        .collect(Collectors.toSet());
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProvidersFromConnectorIds(
      final ForScenarioProvidersWithConnectorIdDefinition element) {
    return getDeclarationsRegistry()
        .getInboundConnectorsByScenarioId(getOrchestrationInfo().getIntegrationScenario().getId())
        .stream()
        .filter(element.getConnectorIds()::contains)
        .collect(Collectors.toSet());
  }
}
