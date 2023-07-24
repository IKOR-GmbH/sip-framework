package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class for calling a scenario consumer specified by it's class */
public final class CallCompositeScenarioConsumerByClassDefinition<R, M>
    extends CallCompositeScenarioConsumerBaseDefinition<
        CallCompositeScenarioConsumerByClassDefinition<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass;

  CallCompositeScenarioConsumerByClassDefinition(
      final R dslReturnDefinition,
      final CompositeProcessDefinition integrationScenario,
      final Class<? extends IntegrationScenarioConsumerDefinition> consumerClass) {
    super(dslReturnDefinition, integrationScenario);
    this.consumerClass = consumerClass;
  }
}