package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

/**
 * DSL class for calling a consumer by its {@link IntegrationScenarioDefinition}
 *
 * @param <S> DSL handle for caller
 * @param <R> DSL handle for the return DSL Verb/type.
 */
interface ProcessConsumerCalls<S extends ProcessConsumerCalls<S, R>, R> {

  CallProcessConsumer<? extends CallProcessConsumer, S> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass);
}
