package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Collections;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public class ForScenarioProvidersWithConnectorIdDefinition<R, M>
    extends ForScenarioProvidersBaseDefinition<
        ForScenarioProvidersWithConnectorIdDefinition<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Set<String> connectorIds;

  ForScenarioProvidersWithConnectorIdDefinition(
      final R dslReturnDefinition,
      final IntegrationScenarioDefinition integrationScenario,
      final Set<String> connectorIds) {
    super(dslReturnDefinition, integrationScenario);
    this.connectorIds = Collections.unmodifiableSet(connectorIds);
  }
}
