package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

public class CallScenarioConsumerWithClassDefinition<R, M>
    extends CallScenarioConsumerBaseDefinition<
        CallScenarioConsumerWithClassDefinition<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioConsumerDefinition> connectorClass;

  protected CallScenarioConsumerWithClassDefinition(
      final R dslReturnDefinition,
      final IntegrationScenarioDefinition integrationScenario,
      final Class<? extends IntegrationScenarioConsumerDefinition> connectorClass) {
    super(dslReturnDefinition, integrationScenario);
    this.connectorClass = connectorClass;
  }
}
