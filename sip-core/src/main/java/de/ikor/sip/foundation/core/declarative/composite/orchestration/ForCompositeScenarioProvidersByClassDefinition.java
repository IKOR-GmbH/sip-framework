package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Collections;
import java.util.Set;

/** DSL class specifying all a scenario provider specified by its class */
public final class ForCompositeScenarioProvidersByClassDefinition<R, M>
    extends ForCompositeScenarioProvidersBaseDefinition<
        ForCompositeScenarioProvidersByClassDefinition<R, M>, R, M> {

  private final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses;

  ForCompositeScenarioProvidersByClassDefinition(
      final R dslReturnDefinition,
      final CompositeProcessDefinition integrationScenario,
      final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses) {
    super(dslReturnDefinition, integrationScenario);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }

  Set<Class<? extends IntegrationScenarioDefinition>> getProviderClasses() {
    return providerClasses;
  }
}
