package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

/**
 * Various handlers use in scenario orchestration *
 *
 * <p><em>For internal use only</em>
 */
@UtilityClass
public class ScenarioOrchestrationHandlers {

  public static ContextInitializer handleContextInitialization(
      final IntegrationScenarioDefinition scenario) {
    return new ContextInitializer(scenario);
  }

  public static <M> ConsumerRequestHandler<M> handleRequestToConsumer(
      final Optional<ScenarioStepRequestExtractor<M>> requestPreparation) {
    return new ConsumerRequestHandler<>(requestPreparation);
  }

  public static <M> ConsumerResponseHandler<M> handleResponseFromConsumer(
      final IntegrationScenarioConsumerDefinition consumer,
      final Optional<StepResultCloner<M>> stepResultCloner,
      final Optional<ScenarioStepResponseConsumer<M>> responseConsumer) {
    return new ConsumerResponseHandler<>(consumer, stepResultCloner, responseConsumer);
  }

  private static <M> ScenarioOrchestrationContext<M> retrieveOrchestrationContext(
      final Exchange exchange) {
    final var context =
        Objects.requireNonNull(
            exchange.getProperty(
                ScenarioOrchestrationContext.PROPERTY_NAME, ScenarioOrchestrationContext.class),
            "Orchestration context for scenario-orchestration could not be retrieved from exchange");
    context.setExchange(exchange);
    return context;
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ContextInitializer {

    private final IntegrationScenarioDefinition integrationScenario;

    @Handler
    public <T> void initializeOrchestrationContext(final T body, final Exchange exchange) {
      exchange.setProperty(
          ScenarioOrchestrationContext.PROPERTY_NAME,
          ScenarioOrchestrationContext.builder()
              .integrationScenario(integrationScenario)
              .originalRequest(body)
              .exchange(exchange)
              .build());
    }
  }

  static class ConsumerRequestHandler<M> {
    private final ScenarioStepRequestExtractor<M> requestPreparation;

    private ConsumerRequestHandler(
        final Optional<ScenarioStepRequestExtractor<M>> requestPreparation) {
      this.requestPreparation =
          requestPreparation.orElseGet(ConsumerRequestHandler::defaultRequestExtractor);
    }

    private static <M> ScenarioStepRequestExtractor<M> defaultRequestExtractor() {
      return ScenarioOrchestrationContext::getOriginalRequest;
    }

    @Handler
    public <T> Object extractRequest(final T body, final Exchange exchange) {
      return requestPreparation.extractStepRequest(retrieveOrchestrationContext(exchange));
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ConsumerResponseHandler<M> {
    private final IntegrationScenarioConsumerDefinition consumer;
    private final Optional<StepResultCloner<M>> stepResultCloner;
    private final Optional<ScenarioStepResponseConsumer<M>> responseConsumer;

    @Handler
    public M handleResponse(final M body, final Exchange exchange) {
      final ScenarioOrchestrationContext<M> context = retrieveOrchestrationContext(exchange);
      context.addResponseForStep(consumer, body, stepResultCloner);
      responseConsumer.ifPresent(c -> c.consumeResponse(body, context));
      return context.getAggregatedResponse().orElse(body);
    }
  }
}
