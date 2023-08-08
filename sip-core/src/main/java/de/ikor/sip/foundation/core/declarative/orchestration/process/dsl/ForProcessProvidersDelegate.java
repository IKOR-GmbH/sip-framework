package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// eq ScenarioConsumerCallsDelegate
/** DSL class specifying a process provider specified by its class */
public final class ForProcessProvidersDelegate<S extends ProcessConsumerCalls<S, R>, R> extends ProcessDslBase<ForProcessProvidersDelegate<S, R>, R>
  implements ProcessConsumerCalls<S, R>{

  @Getter(AccessLevel.PACKAGE)
  private final List<CallableWithinProcessDefinition> consumerCalls = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final S callerNode;
  @Getter(AccessLevel.PACKAGE)
  private final R returningNode;
  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> providerClass;

  ForProcessProvidersDelegate(
          final R dslReturnDefinition,
          final CompositeProcessDefinition compositeProcess,
          final Class<? extends IntegrationScenarioDefinition> providerClass,
          final S definitionNode) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClass = providerClass;
    this.callerNode = definitionNode;
    this.returningNode = dslReturnDefinition;
  }

  @Override
  public CallProcessConsumerImpl<S> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumerImpl<S> def =
        new CallProcessConsumerImpl(getDslReturnDefinition(), getCompositeProcess(), consumerClass);
    consumerCalls.add(def);
    return def;
  }
}
