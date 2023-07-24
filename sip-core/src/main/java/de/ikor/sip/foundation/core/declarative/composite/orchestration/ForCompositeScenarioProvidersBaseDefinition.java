package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

/** DSL base class for specifying which consumers should be called for a scenario provider. */
public abstract sealed class ForCompositeScenarioProvidersBaseDefinition<
        S extends ForCompositeScenarioProvidersBaseDefinition<S, R, M>, R, M>
    extends CompositeScenarioDslDefinitionBase<S, R, M>
    implements CompositeScenarioConsumerCalls<S, R, M>
    permits ForCompositeScenarioProvidersByClassDefinition {

  @Getter(AccessLevel.PACKAGE)
  private final List<CompositeCallableWithinProviderDefinition> nodes = new ArrayList<>();

  @Delegate
  @Getter(AccessLevel.PACKAGE)
  private final CompositeScenarioConsumerCallsDelegate<S, R, M> consumerCallsDelegate =
      new CompositeScenarioConsumerCallsDelegate<>(
          nodes, self(), getDslReturnDefinition(), getIntegrationScenario());

  ForCompositeScenarioProvidersBaseDefinition(
      final R dslReturnDefinition, final CompositeProcessDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  /**
   * Ends the orchestration-definitions for this provider and returns to the previous scope
   *
   * @return Previous scope
   */
  public R endDefinitionForThisProvider() {
    return getDslReturnDefinition();
  }
}
