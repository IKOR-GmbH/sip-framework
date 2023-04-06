package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import lombok.AccessLevel;
import lombok.Getter;

public class CallScenarioConsumerWithClassDefinition<R>
    extends CallScenarioConsumerBaseDefinition<CallScenarioConsumerWithClassDefinition<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioConsumerDefinition> connectorClass;

  protected CallScenarioConsumerWithClassDefinition(
      final R returnElement,
      final Class<? extends IntegrationScenarioConsumerDefinition> connectorClass) {
    super(returnElement);
    this.connectorClass = connectorClass;
  }
}
