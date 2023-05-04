package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScenarioConsumerCallDelegate<S extends ScenarioConsumerCalls<S, R, M>, R, M>
    implements ScenarioConsumerCalls<S, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallScenarioConsumerBaseDefinition<?, ?, M>> scenarioConsumerDefinitions =
      new ArrayList<>();

  final S definitionNode;
  final R returnNode;
  final IntegrationScenarioDefinition integrationScenario;

  @Override
  public CallScenarioConsumerWithConnectorIdDefinition<S, M> callOutboundConnector(
      final String connectorId) {
    final CallScenarioConsumerWithConnectorIdDefinition<S, M> def =
        new CallScenarioConsumerWithConnectorIdDefinition<>(
            definitionNode, integrationScenario, connectorId);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  @Override
  public CallScenarioConsumerWithClassDefinition<S, M> callOutboundConnector(
      final Class<? extends OutboundConnectorDefinition> connectorClass) {
    return callScenarioConsumer(connectorClass);
  }

  @Override
  public CallScenarioConsumerWithClassDefinition<S, M> callScenarioConsumer(
      final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass) {
    final CallScenarioConsumerWithClassDefinition<S, M> def =
        new CallScenarioConsumerWithClassDefinition<>(
            definitionNode, integrationScenario, consumerClass);
    scenarioConsumerDefinitions.add(def);
    return def;
  }

  @Override
  public CallScenarioConsumerCatchAllDefinition<R, M> callAnyUnspecifiedScenarioConsumer() {
    final CallScenarioConsumerCatchAllDefinition<R, M> def =
        new CallScenarioConsumerCatchAllDefinition<>(returnNode, integrationScenario);
    scenarioConsumerDefinitions.add(def);
    return def;
  }
}
