package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
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
import org.apache.camel.model.ProcessorDefinition;

/**
 * Class for generating Camel routes for scenario consumer calls from a DSL
 *
 * <p><em>For internal use only</em>
 */
@Slf4j
@SuppressWarnings("rawtypes")
final class RouteGeneratorForCallCompositeScenarioConsumerDefinition<M>
    extends RouteGeneratorCompositeBase {

  private final CallCompositeScenarioConsumerBaseDefinition<?, ?, M> definitionElement;

  private final Set<IntegrationScenarioDefinition> overallUnhandledConsumers;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Set<IntegrationScenarioDefinition> handledConsumers =
      resolveAndVerifyHandledConsumers();

  RouteGeneratorForCallCompositeScenarioConsumerDefinition(
      final CompositeOrchestrationInfo orchestrationInfo,
      final CallCompositeScenarioConsumerBaseDefinition definitionElement,
      final Set<IntegrationScenarioDefinition> overallUnhandledConsumers) {
    super(orchestrationInfo);
    this.definitionElement = definitionElement;
    this.overallUnhandledConsumers = overallUnhandledConsumers;
  }

  private Set<IntegrationScenarioDefinition> resolveAndVerifyHandledConsumers() {
    final var consumers = resolveHandledConsumers();

    // verify that given providers are not already handled
    final var doubleHandledConsumers =
        consumers.stream().filter(handled -> !overallUnhandledConsumers.contains(handled)).toList();
    if (!doubleHandledConsumers.isEmpty()) {
      log.warn(
          "The following consumers are used more than once in orchestration for scenario '{}': {}",
          getCompositeId(),
          doubleHandledConsumers.stream()
              .map(obj -> obj.getClass().getName())
              .collect(Collectors.joining(",")));
    }
    return consumers;
  }

  private Set<IntegrationScenarioDefinition> resolveHandledConsumers() {
    if (definitionElement instanceof CallCompositeScenarioConsumerByClassDefinition element) {
      return Collections.singleton(retrieveConsumerFromClassDefinition(element));
    }

    throw SIPFrameworkInitializationException.init(
        "Unhandled scenario-consumer definition subclass: %s",
        definitionElement.getClass().getName());
  }

  private IntegrationScenarioDefinition retrieveConsumerFromClassDefinition(
      final CallCompositeScenarioConsumerByClassDefinition element) {
    return getConsumers().stream()
        .filter(consumer -> element.getConsumerClass().equals(consumer.getClass()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Consumer-class '%s' is used on orchestration for integration scenario '%s', but it is not registered with that scenario. Registered outbound connector classes are %s",
                    element.getConsumerClass().getName(),
                    getCompositeId(),
                    getConsumers().stream().map(conn -> conn.getClass().getName()).toList()));
  }

  private List<IntegrationScenarioDefinition> getConsumers() {
    return getDeclarationsRegistry().getCompositeProcessConsumerDefinitions(getCompositeId());
  }

  <T extends ProcessorDefinition<T>> void generateRoute(final T routeDefinition) {
    for (final var consumer : getHandledConsumers()) {
      // prepare request for consumer (as response might still be on the body) and call it
      routeDefinition
          .transform()
          .method(
              CompositeScenarioOrchestrationHandlers.handleRequestToConsumer(
                  consumer, definitionElement.getRequestPreparation()))
          .to(getEndpointForConsumer(consumer));

      // store / aggregate the response and place it on the body
      routeDefinition
          .transform()
          .method(
              CompositeScenarioOrchestrationHandlers.handleResponseFromConsumer(
                  consumer,
                  definitionElement.getStepResultCloner(),
                  definitionElement.getResponseConsumer()));

      overallUnhandledConsumers.remove(consumer);
    }
  }

  private EndpointProducerBuilder getEndpointForConsumer(
      final IntegrationScenarioDefinition consumer) {
    return Objects.requireNonNull(getOrchestrationInfo().getConsumerEndpoints().get(consumer));
  }
}
