package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CompositeScenarioConsumerCallsDelegate<
        S extends CompositeScenarioConsumerCalls<S, R, M>, R, M>
    implements CompositeScenarioConsumerCalls<S, R, M> {

  private final List<CompositeCallableWithinProviderDefinition> consumerDefinitions;
  private final S definitionNode;
  private final R returnNode;
  private final CompositeProcessDefinition integrationScenario;

  @Override
  public CallProcessConsumerByClassDefinition<S, M> callScenarioConsumer(
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumerByClassDefinition<S, M> def =
        new CallProcessConsumerByClassDefinition<>(
            definitionNode, integrationScenario, consumerClass);
    consumerDefinitions.add(def);
    return def;
  }
}
