package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationHandlers;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;

@Slf4j
@SuppressWarnings("rawtypes")
public class RouteGeneratorForCallScenarioConsumerDefinition<M> extends RouteGeneratorBase {

  private final CallScenarioConsumerBaseDefinition<?, ?, M> definitionElement;

  private final Set<IntegrationScenarioConsumerDefinition> overallUnhandledConsumers;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Set<IntegrationScenarioConsumerDefinition> handledConsumers =
      resolveAndVerifyHandledConsumers();

  RouteGeneratorForCallScenarioConsumerDefinition(
      final ScenarioOrchestrationInfo orchestrationInfo,
      final CallScenarioConsumerBaseDefinition definitionElement,
      final Set<IntegrationScenarioConsumerDefinition> overallUnhandledConsumers) {
    super(orchestrationInfo);
    this.definitionElement = definitionElement;
    this.overallUnhandledConsumers = overallUnhandledConsumers;
  }

  private Set<IntegrationScenarioConsumerDefinition> resolveAndVerifyHandledConsumers() {
    final var consumers = resolveHandledConsumers();

    // verify that given providers are not already handled
    final var doubleHandledProviders =
        consumers.stream().filter(handled -> !overallUnhandledConsumers.contains(handled)).toList();
    if (!doubleHandledProviders.isEmpty()) {
      log.warn(
          "The following consumers are used more than once in orchestration for scenario '{}': {}",
          getIntegrationScenarioId(),
          doubleHandledProviders.stream()
              .map(obj -> obj.getClass().getName())
              .collect(Collectors.joining(",")));
    }
    return consumers;
  }

  private Set<IntegrationScenarioConsumerDefinition> resolveHandledConsumers() {
    if (definitionElement instanceof CallScenarioConsumerWithClassDefinition element) {
      return Collections.singleton(retrieveConsumerFromClassDefinition(element));
    } else if (definitionElement instanceof CallScenarioConsumerWithConnectorIdDefinition element) {
      return Collections.singleton(retrieveConsumerFromConnectorIdDefinition(element));
    } else if (definitionElement instanceof CallScenarioConsumerCatchAllDefinition) {
      return Set.copyOf(overallUnhandledConsumers);
    }

    throw SIPFrameworkInitializationException.init(
        "Unhandled scenario-consumer definition subclass: %s",
        definitionElement.getClass().getName());
  }

  private OutboundConnectorDefinition retrieveConsumerFromClassDefinition(
      final CallScenarioConsumerWithClassDefinition element) {
    return getOutboundConnectors().stream()
        .filter(
            outboundConnectorDefinition ->
                element.getConsumerClass().equals(outboundConnectorDefinition.getClass()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Consumer-class '%s' is used on orchestration for integration scenario '%s', but it is not registered with that scenario. Registered outbound connector classes are %s",
                    element.getConsumerClass().getName(),
                    getIntegrationScenarioId(),
                    getOutboundConnectors().stream()
                        .map(conn -> conn.getClass().getName())
                        .toList()));
  }

  private OutboundConnectorDefinition retrieveConsumerFromConnectorIdDefinition(
      final CallScenarioConsumerWithConnectorIdDefinition element) {
    return getOutboundConnectors().stream()
        .filter(
            outboundConnectorDefinition ->
                element.getConnectorId().equals(outboundConnectorDefinition.getId()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Connector ID '%s' is used in orchestration for integration scenario '%s', but it is not registered with that scenario. Registered outbound connectors are %s",
                    element.getConnectorId(),
                    getIntegrationScenarioId(),
                    getOutboundConnectors().stream().map(ConnectorDefinition::getId).toList()));
  }

  private List<OutboundConnectorDefinition> getOutboundConnectors() {
    return getDeclarationsRegistry().getOutboundConnectorsByScenarioId(getIntegrationScenarioId());
  }

  public void generateRoutes(final RouteDefinition routeDefinition) {
    for (final var consumer : getHandledConsumers()) {
      // prepare request for consumer (as response might still be on the body) and call it
      routeDefinition
          .transform()
          .method(
              ScenarioOrchestrationHandlers.handleRequestToConsumer(
                  definitionElement.getRequestPreparation()))
          .to(getEndpointForConsumer(consumer));

      // store / aggregate the response and place it on the body
      routeDefinition
          .transform()
          .method(
              ScenarioOrchestrationHandlers.handleResponseFromConsumer(
                  consumer,
                  definitionElement.getStepResultCloner(),
                  definitionElement.getResponseConsumer()));
    }
  }

  private EndpointProducerBuilder getEndpointForConsumer(
      final IntegrationScenarioConsumerDefinition consumer) {
    return Objects.requireNonNull(getOrchestrationInfo().getConsumerEndpoints().get(consumer));
  }
}
