package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationHandlers;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallNestedCondition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallProcessConsumer;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ProcessOrchestrationDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.RouteGeneratorHelper;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;

/**
 * Class for generating Camel routes for process provider via DSL
 *
 * <p><em>For internal use only</em>
 */
@Slf4j
final class RouteGeneratorForProcessProviders extends RouteGeneratorProcessBase {

  private final ProcessOrchestrationDefinition providerDefinition;

  @Getter(lazy = true)
  private final IntegrationScenarioDefinition handledProvider = resolveHandledProviders();

  RouteGeneratorForProcessProviders(
      final CompositeProcessOrchestrationInfo orchestrationInfo,
      final ProcessOrchestrationDefinition providerDefinition) {
    super(orchestrationInfo);
    this.providerDefinition = providerDefinition;
  }

  private IntegrationScenarioDefinition resolveHandledProviders() {
    return getDeclarationsRegistry().getCompositeProcessProviderDefinition(getCompositeProcessId());
  }

  void generateRoutes(final RoutesDefinition routesDefinition) {

    if (this.getHandledProvider() == null) {
      log.debug(
          "No providers handled by this route-builder for orchestration of integration-scenario {}",
          getCompositeProcessId());
      return;
    }

    final var overallUnhandledScenarioConsumers =
        new HashSet<>(getOrchestrationInfo().getConsumerEndpoints().keySet());

    final var routeDef = generateRouteStart(routesDefinition);
    routeDef.bean(
        CompositeProcessOrchestrationHandlers.handleContextInitialization(getCompositeProcess()));

    for (final var element : RouteGeneratorHelper.getConsumerCalls(providerDefinition)) {
      if (element instanceof CallProcessConsumer<?, ?> ele) {
        new RouteGeneratorForCallProcessConsumer(
                getOrchestrationInfo(), ele, overallUnhandledScenarioConsumers)
            .generateRoute(routeDef);
      } else if (element instanceof CallNestedCondition<?> ele) {
        new RouteGeneratorForCallConditionalProcessConsumer(
                getOrchestrationInfo(), ele, overallUnhandledScenarioConsumers)
            .generateRoute(routeDef);
      }
    }

    routeDef.bean(CompositeProcessOrchestrationHandlers.handleErrorThrownIfNoConsumerWasCalled());

    if (!overallUnhandledScenarioConsumers.isEmpty()) {
      log.warn(
          "Orchestration for integration-scenario '{}' does not call scenario-consumers '{}' for calls coming in from '{}'",
          getCompositeProcessId(),
          overallUnhandledScenarioConsumers.stream()
              .map(consumer -> consumer.getClass().getSimpleName())
              .collect(Collectors.joining(",")),
          this.getHandledProvider().getClass().getSimpleName());
    }
  }

  private RouteDefinition generateRouteStart(final RoutesDefinition routesDefinition) {
    final IntegrationScenarioDefinition provider = this.getHandledProvider();
    final var providerIds = provider.getClass().getSimpleName();
    final var orchestrationRouteId =
        getRoutesRegistry()
            .generateRouteIdForCompositeScenarioOrchestrator(getCompositeProcess(), providerIds);
    return routesDefinition.from(getProviderEndpoint(provider)).routeId(orchestrationRouteId);
  }

  private EndpointConsumerBuilder getProviderEndpoint(
      final IntegrationScenarioDefinition provider) {
    return Objects.requireNonNull(getOrchestrationInfo().getProviderEndpoints().get(provider));
  }
}
