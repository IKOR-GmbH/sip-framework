package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import lombok.AccessLevel;
import lombok.Getter;

public class CallScenarioConsumerWithConnectorIdDefinition<R>
    extends CallScenarioConsumerBaseDefinition<
        CallScenarioConsumerWithConnectorIdDefinition<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final String connectorId;

  protected CallScenarioConsumerWithConnectorIdDefinition(
      final R returnElement, final String connectorId) {
    super(returnElement);
    this.connectorId = connectorId;
  }
}
