package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Collections;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public class ForScenarioProvidersWithClassDefinition<R, M>
    extends ForScenarioProvidersBaseDefinition<
        ForScenarioProvidersWithClassDefinition<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses;

  protected ForScenarioProvidersWithClassDefinition(
      final R dslReturnDefinition,
      final IntegrationScenarioDefinition integrationScenario,
      final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses) {
    super(dslReturnDefinition, integrationScenario);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }
}
