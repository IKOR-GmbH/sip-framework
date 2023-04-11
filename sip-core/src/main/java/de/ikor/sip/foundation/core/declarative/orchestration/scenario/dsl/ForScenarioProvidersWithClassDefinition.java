package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Collections;
import java.util.Set;

public class ForScenarioProvidersWithClassDefinition<R, M>
    extends ForScenarioProvidersBaseDefinition<
        ForScenarioProvidersWithClassDefinition<R, M>, R, M> {

  private final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses;

  ForScenarioProvidersWithClassDefinition(
      final R dslReturnDefinition,
      final IntegrationScenarioDefinition integrationScenario,
      final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses) {
    super(dslReturnDefinition, integrationScenario);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }

  Set<Class<? extends IntegrationScenarioProviderDefinition>> getProviderClasses() {
    return providerClasses;
  }
}
