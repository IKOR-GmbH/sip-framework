package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationHandlers;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallProcessConsumer;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.RouteGeneratorHelper;
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
 * Class for generating Camel routes for process consumer calls from a DSL
 *
 * <p><em>For internal use only</em>
 */
@Slf4j
@SuppressWarnings("rawtypes")
final class RouteGeneratorForCallProcessConsumer extends RouteGeneratorProcessBase {

  private final CallProcessConsumer<?, ?> definitionElement;

  private final Set<IntegrationScenarioDefinition> overallUnhandledConsumers;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Set<IntegrationScenarioDefinition> handledConsumers =
      resolveAndVerifyHandledConsumers();

  RouteGeneratorForCallProcessConsumer(
      final CompositeProcessOrchestrationInfo orchestrationInfo,
      final CallProcessConsumer definitionElement,
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
          getCompositeProcessId(),
          doubleHandledConsumers.stream()
              .map(obj -> obj.getClass().getName())
              .collect(Collectors.joining(",")));
    }
    return consumers;
  }

  private Set<IntegrationScenarioDefinition> resolveHandledConsumers() {
    return Collections.singleton(retrieveConsumerFromClassDefinition(definitionElement));
  }

  private IntegrationScenarioDefinition retrieveConsumerFromClassDefinition(
      final CallProcessConsumer element) {
    return getConsumers().stream()
        .filter(
            consumer -> RouteGeneratorHelper.getConsumerClass(element).equals(consumer.getClass()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Consumer-class '%s' is used on orchestration for process '%s', but it is not registered with that scenario. Registered outbound connector classes are %s",
                    RouteGeneratorHelper.getConsumerClass(element).getName(),
                    getCompositeProcessId(),
                    getConsumers().stream().map(conn -> conn.getClass().getName()).toList()));
  }

  private List<IntegrationScenarioDefinition> getConsumers() {
    return getDeclarationsRegistry()
        .getCompositeProcessConsumerDefinitions(getCompositeProcessId());
  }

  <T extends ProcessorDefinition<T>> void generateRoute(final T routeDefinition) {
    for (final var consumer : getHandledConsumers()) {
      routeDefinition
          .transform()
          .method(
              CompositeProcessOrchestrationHandlers.handleRequestToConsumer(
                  consumer, RouteGeneratorHelper.getRequestPreparation(definitionElement)))
          .to(getEndpointForConsumer(consumer))

          // store / aggregate the response and place it on the body

          .transform()
          .method(
              CompositeProcessOrchestrationHandlers.handleResponseFromConsumer(
                  consumer,
                  RouteGeneratorHelper.getStepResultCloner(definitionElement),
                  RouteGeneratorHelper.getResponseConsumer(definitionElement)));
      overallUnhandledConsumers.remove(consumer);
    }
  }

  private EndpointProducerBuilder getEndpointForConsumer(
      final IntegrationScenarioDefinition consumer) {
    return Objects.requireNonNull(getOrchestrationInfo().getConsumerEndpoints().get(consumer));
  }
}
