package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ForScenarioProvidersBaseDefinition<
        S extends ForScenarioProvidersBaseDefinition<S, R, M>, R, M>
    extends ScenarioDslDefinitionBase<S, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallScenarioConsumerBaseDefinition<?, ?, M>> scenarioConsumerDefinitions =
      new ArrayList<>();

  protected ForScenarioProvidersBaseDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  public CallScenarioConsumerWithConnectorIdDefinition<S, M> callOutboundConnector(
      final String connectorId) {
    final CallScenarioConsumerWithConnectorIdDefinition<S, M> def =
        new CallScenarioConsumerWithConnectorIdDefinition<>(
            self(), getIntegrationScenario(), connectorId);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  public <T extends OutboundConnectorDefinition>
      CallScenarioConsumerWithClassDefinition<S, M> callOutboundConnector(
          final Class<T> connectorClass) {
    return callScenarioConsumer(connectorClass);
  }

  public <T extends IntegrationScenarioConsumerDefinition>
      CallScenarioConsumerWithClassDefinition<S, M> callScenarioConsumer(
          final Class<T> consumerClass) {
    final CallScenarioConsumerWithClassDefinition<S, M> def =
        new CallScenarioConsumerWithClassDefinition<>(
            self(), getIntegrationScenario(), consumerClass);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  public R endCalls() {
    return getDslReturnDefinition();
  }
}
