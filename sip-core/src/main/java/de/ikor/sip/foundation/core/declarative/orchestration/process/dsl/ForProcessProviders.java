package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class specifying all a scenario provider specified by its class */
public final class ForProcessProviders<R, M>
    extends ProcessDslBase<ForProcessProviders<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallProcessConsumer> consumerCalls = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses;

  ForProcessProviders(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }

  public CallProcessConsumer<ForProcessProviders<R, M>, M> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumer<ForProcessProviders<R, M>, M> def =
        new CallProcessConsumer<>(self(), getCompositeProcess(), consumerClass);
    consumerCalls.add(def);
    return def;
  }
}
