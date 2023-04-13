package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL base class for specifying which consumers should be called for a scenario provider. */
public abstract class ForScenarioProvidersBaseDefinition<
        S extends ForScenarioProvidersBaseDefinition<S, R, M>, R, M>
    extends ScenarioDslDefinitionBase<S, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallScenarioConsumerBaseDefinition<?, ?, M>> scenarioConsumerDefinitions =
      new ArrayList<>();

  ForScenarioProvidersBaseDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  /**
   * Specifies that the outbound connector with the given <code>connectorId</code> should be called.
   *
   * @param connectorId Id of the outbound connector
   * @return DSL handle for further call instructions
   */
  public CallScenarioConsumerWithConnectorIdDefinition<S, M> callOutboundConnector(
      final String connectorId) {
    final CallScenarioConsumerWithConnectorIdDefinition<S, M> def =
        new CallScenarioConsumerWithConnectorIdDefinition<>(
            self(), getIntegrationScenario(), connectorId);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  /**
   * Specifies that the outbound connector with the given <code>connectorClass</code> should be
   * called.
   *
   * @param connectorClass Class of the outbound connector
   * @return DSL handle for further call instructions
   */
  public CallScenarioConsumerWithClassDefinition<S, M> callOutboundConnector(
      final Class<? extends OutboundConnectorDefinition> connectorClass) {
    return callScenarioConsumer(connectorClass);
  }

  /**
   * Specifies that the scenario consumer with the given <code>consumerClass</code> should be
   * called.
   *
   * @param consumerClass Class of the consumer
   * @return DSL handle for further call instructions
   */
  public CallScenarioConsumerWithClassDefinition<S, M> callScenarioConsumer(
      final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass) {
    final CallScenarioConsumerWithClassDefinition<S, M> def =
        new CallScenarioConsumerWithClassDefinition<>(
            self(), getIntegrationScenario(), consumerClass);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  /**
   * Specifies that any scenario consumer (which includes outbound connectors) that is attached to
   * the integration scenario but not explicitly defined above will be called.
   *
   * <p>This is a terminal operation for the consumer call specifications, so it needs to be the
   * last call in the list and no additional consumers calls can be specified afterwards.
   *
   * @return DSL handle for further call instructions
   */
  public CallScenarioConsumerCatchAllDefinition<R, M> callAnyUnspecifiedScenarioConsumer() {
    final CallScenarioConsumerCatchAllDefinition<R, M> def =
        new CallScenarioConsumerCatchAllDefinition<>(
            getDslReturnDefinition(), getIntegrationScenario());
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  /**
   * Terminal operation that returns the DSL to the previous scope
   *
   * @return DSL handle
   */
  public R endConsumerCalls() {
    return getDslReturnDefinition();
  }
}
