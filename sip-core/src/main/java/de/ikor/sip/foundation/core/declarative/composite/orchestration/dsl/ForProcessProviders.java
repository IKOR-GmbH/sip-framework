package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

/** DSL class specifying all a scenario provider specified by its class */
public final class ForProcessProviders<R, M> extends ProcessDslBase<ForProcessProviders<R, M>, R, M>
    implements CompositeScenarioConsumerCalls<ForProcessProviders<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CompositeCallableWithinProviderDefinition> consumerCalls = new ArrayList<>();

  @Delegate
  @Getter(AccessLevel.PACKAGE)
  private final CompositeScenarioConsumerCallsImp<ForProcessProviders<R, M>, R, M>
      consumerCallsDelegate =
          new CompositeScenarioConsumerCallsImp<>(
              consumerCalls, self(), getDslReturnDefinition(), getCompositeProcess());

  private final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses;

  ForProcessProviders(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }

  Set<Class<? extends IntegrationScenarioDefinition>> getProviderClasses() {
    return providerClasses;
  }
}
