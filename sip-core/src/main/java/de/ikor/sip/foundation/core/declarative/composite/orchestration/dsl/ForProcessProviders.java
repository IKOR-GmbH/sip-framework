package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Collections;
import java.util.Set;

/** DSL class specifying all a scenario provider specified by its class */
public final class ForProcessProviders<R, M>
    extends ForProcessProvidersBase<ForProcessProviders<R, M>, R, M> {

  private final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses;

  ForProcessProviders(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Set<Class<? extends IntegrationScenarioDefinition>> providerClasses) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }

  Set<Class<? extends IntegrationScenarioDefinition>> getProviderClasses() {
    return providerClasses;
  }
}
