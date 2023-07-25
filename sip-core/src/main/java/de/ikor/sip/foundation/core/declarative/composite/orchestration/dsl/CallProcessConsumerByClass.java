package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class for calling a scenario consumer specified by it's class */
public final class CallProcessConsumerByClass<R, M>
    extends CallProcessConsumerBase<CallProcessConsumerByClass<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> consumerClass;

  CallProcessConsumerByClass(
      final R dslReturnDefinition,
      final CompositeProcessDefinition integrationScenario,
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    super(dslReturnDefinition, integrationScenario);
    this.consumerClass = consumerClass;
  }
}
