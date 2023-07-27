package de.ikor.sip.foundation.core.declarative.orchestration.process;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.CallScenarioConsumerBaseDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepResponseAggregator;

/**
 * Interface to consume or handle the response of an individual scenario consumer call
 *
 * @param <M> Response type of the integration call
 * @see ScenarioStepResponseAggregator
 */
@FunctionalInterface
public interface CompositeProcessStepResponseConsumer<M> {

  /**
   * Consumes or handles the response of an individual scenario consumer call.
   *
   * <p>If an aggregation of the response is required, it is recommended to use {@link
   * CallScenarioConsumerBaseDefinition#andAggregateResponse(ScenarioStepResponseAggregator)}
   * instead.
   *
   * @param latestResponse Response received from the scenario consumer
   * @param context Current orchestration context
   */
  void consumeResponse(M latestResponse, CompositeProcessOrchestrationContext<M> context);
}
