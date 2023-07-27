package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

/**
 * Various handlers use in scenario orchestration *
 *
 * <p><em>For internal use only</em>
 */
@UtilityClass
@Slf4j
public class CompositeProcessOrchestrationHandlers {

  private final String CALLED_CONSUMER_LIST_PROPERTY = "_SipCalledConsumersList";

  public static ContextInitializer handleContextInitialization(
      final CompositeProcessDefinition scenario) {
    return new ContextInitializer(scenario);
  }

  public static <M> ConsumerRequestHandler<M> handleRequestToConsumer(
      final IntegrationScenarioDefinition consumerDefinition,
      final Optional<CompositeProcessStepRequestExtractor<M>> requestPreparation) {
    return new ConsumerRequestHandler<>(consumerDefinition, requestPreparation);
  }

  public static <M> ConsumerResponseHandler<M> handleResponseFromConsumer(
      final IntegrationScenarioDefinition consumer,
      final Optional<StepResultCloner<M>> stepResultCloner,
      final Optional<CompositeProcessStepResponseConsumer<M>> responseConsumer) {
    return new ConsumerResponseHandler<>(consumer, stepResultCloner, responseConsumer);
  }

  public static <M> ContextPredicateHandler<M> handleContextPredicate(
      final Predicate<CompositeProcessOrchestrationContext<M>> predicate) {
    return new ContextPredicateHandler<>(predicate);
  }

  public static ThrowErrorOnUnhandledRequestHandler handleErrorThrownIfNoConsumerWasCalled() {
    return new ThrowErrorOnUnhandledRequestHandler();
  }

  private static <M> CompositeProcessOrchestrationContext<M> retrieveOrchestrationContext(
      final Exchange exchange) {
    final var context =
        Objects.requireNonNull(
            exchange.getProperty(
                CompositeProcessOrchestrationContext.PROPERTY_NAME,
                CompositeProcessOrchestrationContext.class),
            "Orchestration context for scenario-orchestration could not be retrieved from exchange");
    context.setExchange(exchange);
    return context;
  }

  private static List<IntegrationScenarioDefinition> retrieveCalledConsumerList(
      final Exchange exchange) {
    return Objects.requireNonNull(
        exchange.<List>getProperty(CALLED_CONSUMER_LIST_PROPERTY, List.class),
        "Orchestration Exception - Could not retrieve list of called consumers from Exchange");
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ContextInitializer {

    private final CompositeProcessDefinition integrationScenario;

    @Handler
    public <T> void initializeOrchestrationContext(final T body, final Exchange exchange) {
      exchange.setProperty(
          CALLED_CONSUMER_LIST_PROPERTY,
          Collections.synchronizedList(new ArrayList<IntegrationScenarioConsumerDefinition>()));
      exchange.setProperty(
          CompositeProcessOrchestrationContext.PROPERTY_NAME,
          CompositeProcessOrchestrationContext.builder()
              .compositeProcess(integrationScenario)
              .originalRequest(body)
              .exchange(exchange)
              .build());
    }
  }

  static class ConsumerRequestHandler<M> {
    private final IntegrationScenarioDefinition consumerDefinition;
    private final CompositeProcessStepRequestExtractor<M> requestPreparation;

    private ConsumerRequestHandler(
        final IntegrationScenarioDefinition consumerDefinition,
        final Optional<CompositeProcessStepRequestExtractor<M>> requestPreparation) {
      this.consumerDefinition = consumerDefinition;
      this.requestPreparation =
          requestPreparation.orElseGet(ConsumerRequestHandler::defaultRequestExtractor);
    }

    private static <M> CompositeProcessStepRequestExtractor<M> defaultRequestExtractor() {
      return CompositeProcessOrchestrationContext::getOriginalRequest;
    }

    @Handler
    public <T> Object extractRequest(final T body, final Exchange exchange) {
      retrieveCalledConsumerList(exchange).add(consumerDefinition);
      final CompositeProcessOrchestrationContext<M> context =
          retrieveOrchestrationContext(exchange);
      var request = requestPreparation.extractStepRequest(retrieveOrchestrationContext(exchange));
      context.addRequestForStep(consumerDefinition, (M) request, Optional.empty());
      return request;
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ConsumerResponseHandler<M> {
    private final IntegrationScenarioDefinition consumer;
    private final Optional<StepResultCloner<M>> stepResultCloner;
    private final Optional<CompositeProcessStepResponseConsumer<M>> responseConsumer;

    @Handler
    public M handleResponse(final M body, final Exchange exchange) {
      final CompositeProcessOrchestrationContext<M> context =
          retrieveOrchestrationContext(exchange);
      context.addResponseForStep(consumer, body, stepResultCloner);
      responseConsumer.ifPresent(c -> c.consumeResponse(body, context));
      return context.getAggregatedResponse().orElse(body);
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ContextPredicateHandler<M> {
    private final Predicate<CompositeProcessOrchestrationContext<M>> predicate;

    @Handler
    public boolean testPredicate(final Exchange exchange) {
      final CompositeProcessOrchestrationContext<M> context =
          retrieveOrchestrationContext(exchange);
      return predicate.test(context);
    }
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class ThrowErrorOnUnhandledRequestHandler {
    @Handler
    public void checkHandled(final Exchange exchange) {
      if (retrieveCalledConsumerList(exchange).isEmpty()) {
        final var context = retrieveOrchestrationContext(exchange);
        throw SIPFrameworkException.init(
            "No integration-scenario consumer was called during orchestration of integration-scenario '%s'. The orchestration-definition should be modified so that at least one consumer always reacts to a request.",
            context.getCompositeProcess().getId());
      }
    }
  }
}
