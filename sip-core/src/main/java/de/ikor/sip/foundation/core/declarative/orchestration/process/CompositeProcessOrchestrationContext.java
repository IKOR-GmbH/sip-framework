package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.*;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Context used during the orchestration of a composite process.
 *
 * <p>Holds the original request from the provider that initiated the orchestration, as well as a
 * list of requests and responses received from the consumers called during the orchestration.
 *
 * <p>It also supports storing an aggregated response for the integration-call, which can be created
 * during the orchestration-run by integrating consumer-responses via {@link
 * #setAggregatedResponse(Object, Optional)}.
 *
 * @param <M> The response model type of the orchestrated scenario
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CompositeProcessOrchestrationContext<M> {

  /**
   * Record that holds the response for a single orchestration step
   *
   * @param consumer Consumer that was called
   * @param request Request that was sent to the consumer
   * @param response Response as returned by that consumer
   * @param <M> Response model type as defined by the integration scenario
   */
  public record OrchestrationStepResult<M>(
      IntegrationScenarioDefinition consumer, Object request, M response) {}

  /** Key that is used inside Camel's exchange ot save the context. */
  public static final String CAMEL_PROPERTY_NAME = "SipComplexScenarioOrchestrationContext";

  @Getter private final CompositeProcessDefinition compositeProcess;
  private final Object originalRequest;
  private final List<OrchestrationStepResult<M>> orchestrationStepResults =
      Collections.synchronizedList(new ArrayList<>());

  private M aggregatedResponse;

  /** Raw underlying Camel exchange. To be used with care! */
  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PACKAGE)
  private Exchange exchange;

  /**
   * Returns the original request as retrieved from the provider that initiated the integration call
   * with the given type.
   *
   * @param requestType Request model class
   * @return Original request
   * @param <T> Type of the request-model
   */
  public <T> T getOriginalRequest(final Class<T> requestType) {
    return getOriginalRequest();
  }

  /**
   * Returns the original request as retrieved from the provider that initiated the integration
   * call.
   *
   * @return Original request
   * @param <T> Type of the request-model
   */
  @SuppressWarnings("unchecked")
  public <T> T getOriginalRequest() {
    return (T) originalRequest;
  }

  /**
   * Returns the current response payload following this logic:
   *
   * <ul>
   *   <li>If {@link #getAggregatedResponse()} contains a response, it is returned
   *   <li>Otherwise the resposne from the latest step is returned using {@link
   *       #getResultForLatestStep()}
   * </ul>
   *
   * @return Current response
   */
  @Synchronized
  public Optional<M> getLatestResponse() {
    return getAggregatedResponse()
        .or(() -> getResultForLatestStep().map(OrchestrationStepResult::response));
  }

  /**
   * Returns the current aggregated response (if exists)
   *
   * @return aggregated response
   * @see #setAggregatedResponse(Object, Optional)
   */
  @Synchronized
  public Optional<M> getAggregatedResponse() {
    return Optional.ofNullable(aggregatedResponse);
  }

  /**
   * Sets the overall aggregated response for the integration call.
   *
   * @param response the aggregated response
   * @param cloner optional cloner for the response object
   * @return the aggregated response
   * @see #getAggregatedResponse()
   */
  @Synchronized
  public M setAggregatedResponse(final M response, final Optional<StepResultCloner<M>> cloner) {
    aggregatedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    return aggregatedResponse;
  }

  /**
   * Returns the response from the latest called consumer (if exists)
   *
   * @return optional response from latest consumer call
   */
  @Synchronized
  public Optional<OrchestrationStepResult<M>> getResultForLatestStep() {
    return orchestrationStepResults.isEmpty()
        ? Optional.empty()
        : Optional.of(orchestrationStepResults.get(orchestrationStepResults.size() - 1));
  }

  /**
   * Stores a response from the call to a consumer.
   *
   * <p><em>Internal use only</em>
   *
   * @param consumer Consumer that provided the response
   * @param response Response as it was received from the consumer
   * @param cloner Optional cloner for the response
   */
  @Synchronized
  void addResponseForStep(
      final IntegrationScenarioDefinition consumer,
      final M response,
      final Optional<StepResultCloner<M>> cloner) {
    final M maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    getResultOfLastStepFromConsumer(consumer.getClass())
        .ifPresent(
            step -> {
              OrchestrationStepResult<M> lastStep =
                  orchestrationStepResults.remove(orchestrationStepResults.indexOf(step));
              orchestrationStepResults.add(
                  new OrchestrationStepResult<>(consumer, lastStep.request, maybeClonedResponse));
            });
  }

  /**
   * Stores a request from the call to a consumer.
   *
   * <p><em>Internal use only</em>
   *
   * @param consumer Consumer that provided the response
   * @param request Request as it was sent to the consumer
   * @param cloner Optional cloner for the response
   */
  @Synchronized
  void addRequestForStep(
      final IntegrationScenarioDefinition consumer,
      final M request,
      final Optional<StepResultCloner<M>> cloner) {
    final M maybeClonedRequest = cloner.map(c -> c.apply(request)).orElse(request);
    orchestrationStepResults.add(new OrchestrationStepResult<>(consumer, maybeClonedRequest, null));
  }

  @Synchronized
  M addCondition(
          final M response,
          final Optional<StepResultCloner<M>> cloner) {
    final M maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    getResultForLatestStep()
            .ifPresent(
                    step -> {
                      OrchestrationStepResult<M> lastStep =
                              orchestrationStepResults.remove(orchestrationStepResults.indexOf(step));
                      orchestrationStepResults.add(
                              new OrchestrationStepResult<>(null, lastStep.request, maybeClonedResponse));
                    });
    return maybeClonedResponse;
  }

  /**
   * @return Unmodifiable List of responses received from consumers so far
   */
  public List<OrchestrationStepResult<M>> getOrchestrationStepResults() {
    return Collections.unmodifiableList(orchestrationStepResults);
  }

  /**
   * Retrieves the <em>first</em> result returned by the consumer specified by its {@code
   * consumerClass}. The result holds both request and response objects.
   *
   * @param consumerClass Class of the consumer for which to retrieve consumer
   * @return Optional first response of this consumer
   */
  public Optional<OrchestrationStepResult<M>> getResultOfFirstStepFromConsumer(
      final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass) {
    return orchestrationStepResults.stream()
        .filter(step -> consumerClass.isInstance(step.consumer()))
        .findFirst();
  }

  /**
   * Retrieves the <em>last</em> response returned by the consumer specified by its {@code
   * consumerClass}. The result holds both request and response objects.
   *
   * @param consumerClass Class of the consumer for which to retrieve consumer
   * @return Optional last response of this consumer
   */
  public Optional<OrchestrationStepResult<M>> getResultOfLastStepFromConsumer(
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    return orchestrationStepResults.stream()
        .filter(step -> consumerClass.isInstance(step.consumer()))
        .reduce((first, second) -> second);
  }

  /**
   * Returns the header with the given name and type, if it exists. It is a Camel's low level header
   * and should be used with care.
   *
   * @param name Name of the header to retrieve
   * @param type Header type class
   * @return Optional containing the header if it exists
   * @param <T> Header type
   * @see org.apache.camel.Message#getHeader(String, Class)
   */
  public <T> Optional<T> getHeader(final String name, final Class<T> type) {
    return Optional.ofNullable(getExchange().getMessage().getHeader(name, type));
  }
}
