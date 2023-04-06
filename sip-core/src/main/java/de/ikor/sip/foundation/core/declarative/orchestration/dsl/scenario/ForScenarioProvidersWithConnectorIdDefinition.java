package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import java.util.Collections;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public class ForScenarioProvidersWithConnectorIdDefinition<R>
    extends ForScenarioProvidersBaseDefinition<
        ForScenarioProvidersWithConnectorIdDefinition<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final Set<String> connectorIds;

  protected ForScenarioProvidersWithConnectorIdDefinition(
      final R returnElement, final Set<String> connectorIds) {
    super(returnElement);
    this.connectorIds = Collections.unmodifiableSet(connectorIds);
  }
}
