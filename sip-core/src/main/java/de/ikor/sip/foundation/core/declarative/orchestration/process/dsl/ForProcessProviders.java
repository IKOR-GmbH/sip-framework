package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** DSL class specifying a process provider specified by its class */
public final class ForProcessProviders<R> extends ProcessDslBase<ForProcessProviders<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallProcessConsumer<ForProcessProviders<R>>> consumerCalls = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> providerClass;

  ForProcessProviders(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> providerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClass = providerClass;
  }

  public CallProcessConsumer<ForProcessProviders<R>> callConsumer(
      Class<? extends IntegrationScenarioDefinition> consumerClass) {
    final CallProcessConsumer<ForProcessProviders<R>> def =
        new CallProcessConsumer(getDslReturnDefinition(), getCompositeProcess(), consumerClass);
    consumerCalls.add(def);
    return def;
  }
}
