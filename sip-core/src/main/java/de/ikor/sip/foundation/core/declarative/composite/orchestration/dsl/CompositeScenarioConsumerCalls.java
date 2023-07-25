package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

/** Interface containing various methods to trigger a call to an integration-scenario consumer. * */
sealed interface CompositeScenarioConsumerCalls<
        S extends CompositeScenarioConsumerCalls<S, R, M>, R, M>
    permits ForProcessProviders, CompositeScenarioConsumerCallsImp {

  /**
   * Specifies that the scenario consumer with the given <code>consumerClass</code> should be
   * called.
   *
   * @param consumerClass Class of the consumer
   * @return DSL handle for further call instructions
   */
  CallProcessConsumer<S, M> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass);
}
