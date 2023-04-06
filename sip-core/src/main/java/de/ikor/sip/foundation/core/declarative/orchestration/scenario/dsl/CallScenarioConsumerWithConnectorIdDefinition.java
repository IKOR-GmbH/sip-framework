package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

public class CallScenarioConsumerWithConnectorIdDefinition<R, M>
    extends CallScenarioConsumerBaseDefinition<
        CallScenarioConsumerWithConnectorIdDefinition<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final String connectorId;

  CallScenarioConsumerWithConnectorIdDefinition(
      final R dslReturnDefinition,
      final IntegrationScenarioDefinition integrationScenario,
      final String connectorId) {
    super(dslReturnDefinition, integrationScenario);
    this.connectorId = connectorId;
  }
}
