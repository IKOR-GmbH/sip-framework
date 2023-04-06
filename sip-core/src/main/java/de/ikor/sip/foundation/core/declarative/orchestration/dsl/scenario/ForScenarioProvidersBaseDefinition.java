package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ForScenarioProvidersBaseDefinition<
        S extends ForScenarioProvidersBaseDefinition<S, R>, R>
    extends DslDefinitionBase<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallScenarioConsumerBaseDefinition<?, ?>> scenarioConsumerDefinitions =
      new ArrayList<>();

  protected ForScenarioProvidersBaseDefinition(final R dslReturnDefinition) {
    super(dslReturnDefinition);
  }

  public CallScenarioConsumerWithConnectorIdDefinition<S> callOutboundConnector(
      final String connectorId) {
    final CallScenarioConsumerWithConnectorIdDefinition<S> def =
        new CallScenarioConsumerWithConnectorIdDefinition<>(self(), connectorId);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  public <T extends OutboundConnectorDefinition>
      CallScenarioConsumerWithClassDefinition<S> callOutboundConnector(
          final Class<T> connectorClass) {
    return callScenarioConsumer(connectorClass);
  }

  public <T extends IntegrationScenarioConsumerDefinition>
      CallScenarioConsumerWithClassDefinition<S> callScenarioConsumer(
          final Class<T> consumerClass) {
    final CallScenarioConsumerWithClassDefinition<S> def =
        new CallScenarioConsumerWithClassDefinition<>(self(), consumerClass);
    scenarioConsumerDefinitions.add(def);
    return def;
  }
}
