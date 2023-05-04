package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;

public interface ScenarioConsumerCalls<S extends ScenarioConsumerCalls<S, R, M>, R, M> {

  /**
   * Specifies that the outbound connector with the given <code>connectorId</code> should be called.
   *
   * @param connectorId Id of the outbound connector
   * @return DSL handle for further call instructions
   */
  CallScenarioConsumerWithConnectorIdDefinition<S, M> callOutboundConnector(String connectorId);

  /**
   * Specifies that the outbound connector with the given <code>connectorClass</code> should be
   * called.
   *
   * @param connectorClass Class of the outbound connector
   * @return DSL handle for further call instructions
   */
  CallScenarioConsumerWithClassDefinition<S, M> callOutboundConnector(
      Class<? extends OutboundConnectorDefinition> connectorClass);

  /**
   * Specifies that the scenario consumer with the given <code>consumerClass</code> should be
   * called.
   *
   * @param consumerClass Class of the consumer
   * @return DSL handle for further call instructions
   */
  CallScenarioConsumerWithClassDefinition<S, M> callScenarioConsumer(
      Class<? extends IntegrationScenarioConsumerDefinition> consumerClass);

  /**
   * Specifies that any scenario consumer (which includes outbound connectors) that is attached to
   * the integration scenario but not explicitly defined above will be called.
   *
   * <p>This is a terminal operation for the consumer call specifications, so it needs to be the
   * last call in the list and no additional consumers calls can be specified afterwards.
   *
   * @return DSL handle for further call instructions
   */
  CallScenarioConsumerCatchAllDefinition<R, M> callAnyUnspecifiedScenarioConsumer();
}
