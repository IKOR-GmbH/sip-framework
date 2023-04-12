package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.OrchestrationContext;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioOrchestrationContext;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

@UtilityClass
public class ScenarioOrchestrationHandlers {

  public static ContextInitializer buildHandlerForScenarioContextInitialization(
      final IntegrationScenarioDefinition scenario) {
    return new ContextInitializer(scenario);
  }

  public static <M> ConsumerRequestHandler<M> buildHandlerForConsumerRequestInitialization(
      final Optional<ScenarioStepRequestExtractor<M>> requestPreparation) {
    return new ConsumerRequestHandler<>(requestPreparation);
  }

  public static <M> Object buildHandlerForConsumerResponseAggregation(
      final IntegrationScenarioConsumerDefinition consumer,
      final Optional<StepResultCloner<M>> stepResultCloner,
      final Optional<ScenarioStepResponseConsumer<M>> responseConsumer) {
    return new ConsumerResponseHandler<>(consumer, stepResultCloner, responseConsumer);
  }

  private static ScenarioOrchestrationContext retrieveOrchestrationContext(
      final Exchange exchange) {
    return exchange.getProperty(
        ScenarioOrchestrationContext.PROPERTY_NAME, ScenarioOrchestrationContext.class);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ContextInitializer {

    private final IntegrationScenarioDefinition integrationScenario;

    @Handler
    public <T> ScenarioOrchestrationContext initializeOrchestrationContext(final T body) {
      return new ScenarioOrchestrationContext(integrationScenario, body);
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
      return OrchestrationContext::getOriginalRequest;
    }

    @Handler
    public <T> M extractRequest(final T body, final Exchange exchange) {
      return requestPreparation.extractStepRequest(retrieveOrchestrationContext(exchange));
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class ConsumerResponseHandler<M> {

    private final IntegrationScenarioConsumerDefinition consumer;
    private final Optional<StepResultCloner<M>> stepResultCloner;
    private final Optional<ScenarioStepResponseConsumer<M>> responseConsumer;

    public Object handleResponse(final M body, final Exchange exchange) {
      final var context = retrieveOrchestrationContext(exchange);
      context.addResponseForStep(consumer, body, stepResultCloner);

      responseConsumer.ifPresent(c -> c.consumeResponse(body, context));

      return context.getAggregatedResponse().orElse(body);
    }
  }
}
