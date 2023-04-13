package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationHandlers;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
public class RouteGeneratorForScenarioProvidersDefinition<M> extends RouteGeneratorBase {
  private final ForScenarioProvidersBaseDefinition<?, ?, M> providerDefinition;
  private final Set<IntegrationScenarioProviderDefinition> overallUnhandledProviders;

  @Getter(lazy = true)
  private final Set<IntegrationScenarioProviderDefinition> handledProviders =
      resolveAndVerifyHandledProviders();

  RouteGeneratorForScenarioProvidersDefinition(
      final ScenarioOrchestrationInfo orchestrationInfo,
      final ForScenarioProvidersBaseDefinition providerDefinition,
      final Set<IntegrationScenarioProviderDefinition> overallUnhandledProviders) {
    super(orchestrationInfo);
    this.providerDefinition = providerDefinition;
    this.overallUnhandledProviders = overallUnhandledProviders;
  }

  private Set<IntegrationScenarioProviderDefinition> resolveAndVerifyHandledProviders() {
    final var providers = resolveHandledProviders();

    // verify that given providers are not already handled
    final var doubleHandledProviders =
        providers.stream().filter(handled -> !overallUnhandledProviders.contains(handled)).toList();
    if (!doubleHandledProviders.isEmpty()) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "The following providers are used more than once in orchestration for scenario '%s': %s",
              getIntegrationScenarioId(),
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
      return Set.copyOf(overallUnhandledProviders);
    }
    throw new SIPFrameworkInitializationException(
        String.format(
            "Unhandled scenario-provider subclass: %s", providerDefinition.getClass().getName()));
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProvidersFromClasses(
      final ForScenarioProvidersWithClassDefinition element) {
    final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses =
        element.getProviderClasses();
    final var scenarioProviderMap =
        getDeclarationsRegistry()
            .getInboundConnectorsByScenarioId(getIntegrationScenarioId())
            .stream()
            .collect(Collectors.toMap(InboundConnectorDefinition::getClass, con -> con));
    final var unknownProviderNames =
        providerClasses.stream()
            .filter(clazz -> !scenarioProviderMap.containsKey(clazz))
            .map(ele -> ele.getClass().getName())
            .toList();
    if (!unknownProviderNames.isEmpty()) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "The following provider-classes are used in orchestration for scenario '%s', but not registered with that scenario: %s",
              getIntegrationScenarioId(), String.join(",", unknownProviderNames)));
    }
    return providerClasses.stream().map(scenarioProviderMap::get).collect(Collectors.toSet());
  }

  private Set<IntegrationScenarioProviderDefinition> resolveProvidersFromConnectorIds(
      final ForScenarioProvidersWithConnectorIdDefinition<?, M> element) {
    final Set<String> connectorIds = element.getConnectorIds();
    final var scenarioIdMap =
        getDeclarationsRegistry()
            .getInboundConnectorsByScenarioId(getIntegrationScenarioId())
            .stream()
            .collect(Collectors.toMap(ConnectorDefinition::getId, con -> con));
    final var unknownIds =
        connectorIds.stream().filter(id -> !scenarioIdMap.containsKey(id)).toList();
    if (!unknownIds.isEmpty()) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "The following connector-IDs are used in orchestration for scenario '%s', but not registered with that scenario: %s",
              getIntegrationScenarioId(), String.join(",", unknownIds)));
    }
    return connectorIds.stream().map(scenarioIdMap::get).collect(Collectors.toSet());
  }

  void generateRoutes(final RoutesDefinition routesDefinition) {

    if (getHandledProviders().isEmpty()) {
      log.debug(
          "No providers handled by this route-builder for orchestration of integration-scenario {}",
          getIntegrationScenarioId());
      return;
    }

    final var consumerDefinitions = providerDefinition.getScenarioConsumerDefinitions();
    final var overallUnhandledScenarioConsumers =
        new HashSet<>(getOrchestrationInfo().getConsumerEndpoints().keySet());
    final List<RouteGeneratorForCallScenarioConsumerDefinition> consumerBuilders =
        new ArrayList<>();
    for (final var consumerDef : consumerDefinitions) {
      final var consumerBuilder =
          new RouteGeneratorForCallScenarioConsumerDefinition<M>(
              getOrchestrationInfo(),
              consumerDef,
              Collections.unmodifiableSet(overallUnhandledScenarioConsumers));
      if (consumerBuilder.getHandledConsumers().isEmpty()) {
        continue;
      }
      overallUnhandledScenarioConsumers.removeAll(consumerBuilder.getHandledConsumers());
      consumerBuilders.add(consumerBuilder);
    }

    if (!overallUnhandledScenarioConsumers.isEmpty()) {
      log.warn(
          "Orchestration for integration-scenario '{}' does not call scenario-consumers '{}' for calls coming in from '{}'",
          getIntegrationScenarioId(),
          overallUnhandledScenarioConsumers.stream()
              .map(consumer -> consumer.getClass().getSimpleName())
              .collect(Collectors.joining(",")),
          getHandledProviders().stream()
              .map(provider -> provider.getClass().getSimpleName())
              .collect(Collectors.joining(",")));
    }

    final var routeStart = generateRouteStart(routesDefinition);
    routeStart.bean(
        ScenarioOrchestrationHandlers.handleContextInitialization(getIntegrationScenario()));
    consumerBuilders.forEach(consumerBuilder -> consumerBuilder.generateRoutes(routeStart));
  }

  private RouteDefinition generateRouteStart(final RoutesDefinition routesDefinition) {
    final Set<IntegrationScenarioProviderDefinition> providers = getHandledProviders();
    final var providerIds =
        providers.stream()
            .map(prov -> prov.getClass().getSimpleName())
            .collect(Collectors.joining("-"));
    final var orchestrationRouteId =
        getRoutesRegistry()
            .generateRouteIdForScenarioOrchestrator(getIntegrationScenario(), providerIds);
    if (providers.size() == 1) {
      return routesDefinition
          .from(getProviderEndpoint(providers.iterator().next()))
          .routeId(orchestrationRouteId);
    }
    final var directName =
        String.format(
            "sip-scenario-orchestrator-%s-merge-%s", getIntegrationScenarioId(), providerIds);
    for (final var provider : providers) {
      routesDefinition
          .from(getProviderEndpoint(provider))
          .routeId(
              getRoutesRegistry()
                  .generateRouteIdForScenarioOrchestrator(
                      getIntegrationScenario(), "merge", provider.getClass().getSimpleName()))
          .to(StaticEndpointBuilders.direct(directName));
    }
    return routesDefinition
        .from(StaticEndpointBuilders.direct(directName))
        .routeId(orchestrationRouteId);
  }

  private EndpointConsumerBuilder getProviderEndpoint(
      final IntegrationScenarioProviderDefinition provider) {
    return Objects.requireNonNull(getOrchestrationInfo().getProviderEndpoints().get(provider));
  }
}
