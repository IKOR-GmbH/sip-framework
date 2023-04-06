package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.Collections;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public class ForScenarioProvidersWithClassDefinition<R>
    extends ForScenarioProvidersBaseDefinition<ForScenarioProvidersWithClassDefinition<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses;

  protected ForScenarioProvidersWithClassDefinition(
      final R returnElement,
      final Set<Class<? extends IntegrationScenarioProviderDefinition>> providerClasses) {
    super(returnElement);
    this.providerClasses = Collections.unmodifiableSet(providerClasses);
  }
}
