package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * DSL class for specifying orchestration of scenario providers
 *
 * @param <M> The response model type of the integration scenario
 */
public class CompositeScenarioOrchestrationDefinition<M>
    extends CompositeScenarioDslDefinitionBase<
        CompositeScenarioOrchestrationDefinition<M>, EndOfDsl, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForCompositeScenarioProvidersBaseDefinition<?, ?, M>>
      scenarioProviderDefinitions = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private boolean catchAllAdded = false;

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param integrationScenario Integration scenario
   */
  public CompositeScenarioOrchestrationDefinition(
      final CompositeProcessDefinition integrationScenario) {
    super(null, integrationScenario);
  }

  /**
   * Specifies scenario providers that should be orchestrated further by their connector class.
   *
   * <p>No order of execution is guaranteed if more than one class is provided - call this function
   * multiple times if this is necessary.
   *
   * @param providerClass The class(es) of the inbound connector
   * @return DSL handle for specifying consumer calls
   */
  public ForCompositeScenarioProvidersByClassDefinition<
          CompositeScenarioOrchestrationDefinition<M>, M>
      forScenarioProviders(
          final Class<? extends IntegrationScenarioProviderDefinition>... providerClass) {
    verifyNoCatchAllOrThrow();
    final ForCompositeScenarioProvidersByClassDefinition<
            CompositeScenarioOrchestrationDefinition<M>, M>
        def =
            new ForCompositeScenarioProvidersByClassDefinition<>(
                self(), getIntegrationScenario(), Set.of(providerClass));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  private void verifyNoCatchAllOrThrow() {
    if (catchAllAdded) {
      throw new SIPFrameworkException(
          "Catch-all definition already added via method forAnyUnspecifiedProvider(). No further definitions allowed.");
    }
  }
}
