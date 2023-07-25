package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CompositeScenarioConsumerCallsImp<
        S extends CompositeScenarioConsumerCalls<S, R, M>, R, M>
    implements CompositeScenarioConsumerCalls<S, R, M> {

  private final List<CompositeCallableWithinProviderDefinition> consumerDefinitions;
  private final S definitionNode;
  private final R returnNode;
  private final CompositeProcessDefinition compositeProcess;

  @Override
  public CallProcessConsumer<S, M> callConsumer(
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumer<S, M> def =
        new CallProcessConsumer<>(definitionNode, compositeProcess, consumerClass);
    consumerDefinitions.add(def);
    return def;
  }
}
