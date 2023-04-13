package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

/** DSL class specifying all remaining scenario providers */
public class ForScenarioProvidersCatchAllDefinition<R, M>
    extends ForScenarioProvidersBaseDefinition<ForScenarioProvidersCatchAllDefinition<R, M>, R, M> {

  ForScenarioProvidersCatchAllDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }
}
