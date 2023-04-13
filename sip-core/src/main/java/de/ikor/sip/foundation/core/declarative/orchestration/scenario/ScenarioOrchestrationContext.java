package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

/**
 * Context used during the orchestration of an integration-scenario.
 *
 * <p>Holds the original request from the provider that initiated the orchestration, as well as a
 * list of responses received from the consumers called during the orchestration.
 *
 * <p>It also supports storing an aggregated response for the integration-call, which can be created
 * during the orchestration-run by integrating consumer-responses via {@link
 * #setAggregatedResponse(Object, Optional)}.
 *
 * @param <M> The response model type of the orchestrated scenario
 */
@RequiredArgsConstructor
public class ScenarioOrchestrationContext<M> {

  public static final String PROPERTY_NAME = "SipScenarioOrchestrationContext";

  @Getter private final IntegrationScenarioDefinition integrationScenario;
  private final Object originalRequest;
  private final List<OrchestrationStepResponse<M>> orchestrationStepResponses =
      Collections.synchronizedList(new ArrayList<>());
  private M aggregatedResponse;

  /**
   * Returns the request as retrieved from the provider that initiated the integration call.
   *
   * @return the original request
   * @param <T> Type of the request-model
   */
  public <T> T getOriginalRequest() {
    return (T) originalRequest;
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
  public Optional<OrchestrationStepResponse<M>> getResponseForLatestStep() {
    return orchestrationStepResponses.isEmpty()
        ? Optional.empty()
        : Optional.of(orchestrationStepResponses.get(orchestrationStepResponses.size() - 1));
  }

  /**
   * Stores a response from the call to a consumer.
   *
   * <p><em>Internal use only</em>
   *
   * @param consumer Consumer that provided the response
   * @param response Response as it was received from the consumer
   * @param cloner Optional cloner for the response
   * @return The response
   */
  @Synchronized
  public M addResponseForStep(
      final IntegrationScenarioConsumerDefinition consumer,
      final M response,
      final Optional<StepResultCloner<M>> cloner) {
    final M maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    orchestrationStepResponses.add(new OrchestrationStepResponse<>(consumer, maybeClonedResponse));
    return maybeClonedResponse;
  }

  /**
   * @return Unmodifiable List of responses received from consumers so far
   */
  public List<OrchestrationStepResponse<M>> getOrchestrationStepResponses() {
    return Collections.unmodifiableList(orchestrationStepResponses);
  }

  /**
   * Record that holds the response for a single orchestration step
   *
   * @param consumer Consumer that was called
   * @param result Response as received by that consumer
   * @param <M> Response model type as defined by the integration scenario
   */
  public record OrchestrationStepResponse<M>(
      IntegrationScenarioConsumerDefinition consumer, M result) {}
}
