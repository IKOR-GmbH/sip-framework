package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

public class ForScenarioProvidersCatchAllDefinition<R, M>
    extends ForScenarioProvidersBaseDefinition<ForScenarioProvidersCatchAllDefinition<R, M>, R, M> {

  protected ForScenarioProvidersCatchAllDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }
}
