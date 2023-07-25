package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;

/**
 * Class for generating Camel routes for scenario providers via DSL
 *
 * <p><em>For internal use only</em>
 */
@Slf4j
@SuppressWarnings("rawtypes")
final class RouteGeneratorForCompositeScenarioProvidersDefinition<M>
    extends RouteGeneratorCompositeBase {
  private final ForProcessProvidersBaseDefinition<?, ?, M> providerDefinition;
  private final Set<IntegrationScenarioDefinition> overallUnhandledProviders;

  @Getter(lazy = true)
  private final Set<IntegrationScenarioDefinition> handledProviders =
      resolveAndVerifyHandledProviders();

  RouteGeneratorForCompositeScenarioProvidersDefinition(
      final CompositeOrchestrationInfo orchestrationInfo,
      final ForProcessProvidersBaseDefinition providerDefinition,
      final Set<IntegrationScenarioDefinition> overallUnhandledProviders) {
    super(orchestrationInfo);
    this.providerDefinition = providerDefinition;
    this.overallUnhandledProviders = overallUnhandledProviders;
  }

  private Set<IntegrationScenarioDefinition> resolveAndVerifyHandledProviders() {
    final var providers = resolveHandledProviders();

    // verify that given providers are not already handled
    final var doubleHandledProviders =
        providers.stream().filter(handled -> !overallUnhandledProviders.contains(handled)).toList();
    if (!doubleHandledProviders.isEmpty()) {
      throw SIPFrameworkInitializationException.init(
          "The following providers are used more than once in orchestration for scenario '%s': %s",
          getCompositeId(),
          doubleHandledProviders.stream()
              .map(obj -> obj.getClass().getName())
              .collect(Collectors.joining(",")));
    }

    return providers;
  }

  private Set<IntegrationScenarioDefinition> resolveHandledProviders() {
    if (providerDefinition instanceof ForProcessProvidersByClassDefinition element) {
      return resolveProvidersFromClasses(element);
    }
    throw SIPFrameworkInitializationException.init(
        "Unhandled scenario-provider subclass: %s", providerDefinition.getClass().getName());
  }

  private Set<IntegrationScenarioDefinition> resolveProvidersFromClasses(
      final ForProcessProvidersByClassDefinition element) {
    final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses =
        element.getProviderClasses();

    final var scenarioProviderMap =
        getDeclarationsRegistry().getCompositeProcessProviderDefinitions(getCompositeId()).stream()
            .collect(Collectors.toMap(IntegrationScenarioDefinition::getClass, con -> con));
    final var unknownProviderNames =
        providerClasses.stream()
            .filter(clazz -> !scenarioProviderMap.containsKey(clazz))
            .map(Class::getName)
            .toList();
    if (!unknownProviderNames.isEmpty()) {
      throw SIPFrameworkInitializationException.init(
          "The following provider-classes are used in orchestration for scenario '%s', but not registered with that scenario: %s",
          getCompositeId(), String.join(",", unknownProviderNames));
    }
    return providerClasses.stream().map(scenarioProviderMap::get).collect(Collectors.toSet());
  }

  void generateRoutes(final RoutesDefinition routesDefinition) {

    if (getHandledProviders().isEmpty()) {
      log.debug(
          "No providers handled by this route-builder for orchestration of integration-scenario {}",
          getCompositeId());
      return;
    }

    final var overallUnhandledScenarioConsumers =
        new HashSet<>(getOrchestrationInfo().getConsumerEndpoints().keySet());

    final var routeDef = generateRouteStart(routesDefinition);
    routeDef.bean(
        CompositeScenarioOrchestrationHandlers.handleContextInitialization(getCompositeProcess()));

    for (final var element : providerDefinition.getNodes()) {
      if (element instanceof CallProcessConsumerBaseDefinition callDef) {
        new RouteGeneratorForCallCompositeScenarioConsumerDefinition<M>(
                getOrchestrationInfo(), callDef, overallUnhandledScenarioConsumers)
            .generateRoute(routeDef);
      } else {
        throw SIPFrameworkInitializationException.init(
            "No handling defined for type %s used in orchestration for scenario %s",
            element.getClass().getName(), getCompositeId());
      }
    }

    routeDef.bean(CompositeScenarioOrchestrationHandlers.handleErrorThrownIfNoConsumerWasCalled());

    if (!overallUnhandledScenarioConsumers.isEmpty()) {
      log.warn(
          "Orchestration for integration-scenario '{}' does not call scenario-consumers '{}' for calls coming in from '{}'",
          getCompositeId(),
          overallUnhandledScenarioConsumers.stream()
              .map(consumer -> consumer.getClass().getSimpleName())
              .collect(Collectors.joining(",")),
          getHandledProviders().stream()
              .map(provider -> provider.getClass().getSimpleName())
              .collect(Collectors.joining(",")));
    }
  }

  private RouteDefinition generateRouteStart(final RoutesDefinition routesDefinition) {
    final Set<IntegrationScenarioDefinition> providers = getHandledProviders();
    final var providerIds =
        providers.stream()
            .map(prov -> prov.getClass().getSimpleName())
            .collect(Collectors.joining("-"));
    final var orchestrationRouteId =
        getRoutesRegistry()
            .generateRouteIdForCompositeScenarioOrchestrator(getCompositeProcess(), providerIds);
    if (providers.size() == 1) {
      return routesDefinition
          .from(getProviderEndpoint(providers.iterator().next()))
          .routeId(orchestrationRouteId);
    }
    final var directName =
        String.format("sip-scenario-orchestrator-%s-merge-%s", getCompositeId(), providerIds);
    for (final var provider : providers) {
      routesDefinition
          .from(getProviderEndpoint(provider))
          .routeId(
              getRoutesRegistry()
                  .generateRouteIdForCompositeScenarioOrchestrator(
                      getCompositeProcess(), "merge", provider.getClass().getSimpleName()))
          .to(StaticEndpointBuilders.direct(directName));
    }
    return routesDefinition
        .from(StaticEndpointBuilders.direct(directName))
        .routeId(orchestrationRouteId);
  }

  private EndpointConsumerBuilder getProviderEndpoint(
      final IntegrationScenarioDefinition provider) {
    return Objects.requireNonNull(getOrchestrationInfo().getProviderEndpoints().get(provider));
  }
}
