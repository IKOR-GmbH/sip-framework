package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

/** DSL base class for specifying which consumers should be called for a scenario provider. */
public abstract class ForScenarioProvidersBaseDefinition<
        S extends ForScenarioProvidersBaseDefinition<S, R, M>, R, M>
    extends ScenarioDslDefinitionBase<S, R, M> implements ScenarioConsumerCalls<S, R, M> {

  @Delegate
  @Getter(AccessLevel.PACKAGE)
  private final ScenarioConsumerCallDelegate<S, R, M> consumerCallsDelegate =
      new ScenarioConsumerCallDelegate<>(
          self(), getDslReturnDefinition(), getIntegrationScenario());

  ForScenarioProvidersBaseDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  public R endDefinitionForThisProvider() {
    return getDslReturnDefinition();
  }
}
