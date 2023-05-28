package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ScenarioConsumerCallsDelegate<S extends ScenarioConsumerCalls<S, R, M>, R, M>
    implements ScenarioConsumerCalls<S, R, M> {

  private final List<CallableWithinProviderDefinition> consumerDefinitions;
  private final S definitionNode;
  private final R returnNode;
  private final IntegrationScenarioDefinition integrationScenario;

  @Override
  public CallScenarioConsumerByConnectorIdDefinition<S, M> callOutboundConnector(
      final String connectorId) {
    final CallScenarioConsumerByConnectorIdDefinition<S, M> def =
        new CallScenarioConsumerByConnectorIdDefinition<>(
            definitionNode, integrationScenario, connectorId);
    consumerDefinitions.add(def);
    return def;
  }

  @Override
  public CallScenarioConsumerByClassDefinition<S, M> callOutboundConnector(
      final Class<? extends OutboundConnectorDefinition> connectorClass) {
    return callScenarioConsumer(connectorClass);
  }

  @Override
  public CallScenarioConsumerByClassDefinition<S, M> callScenarioConsumer(
      final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass) {
    final CallScenarioConsumerByClassDefinition<S, M> def =
        new CallScenarioConsumerByClassDefinition<>(
            definitionNode, integrationScenario, consumerClass);
    consumerDefinitions.add(def);
    return def;
  }
}
