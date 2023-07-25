package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
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
public class ProcessOrchestrationDefinition<M>
    extends ProcessDslDefinitionBase<ProcessOrchestrationDefinition<M>, EndOfDsl, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForProcessProvidersBaseDefinition<?, ?, M>> scenarioProviderDefinitions =
      new ArrayList<>();

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param integrationScenario Integration scenario
   */
  public ProcessOrchestrationDefinition(final CompositeProcessDefinition integrationScenario) {
    super(null, integrationScenario);
  }

  /**
   * Specifies scenario providers that should be orchestrated further by their connector class.
   *
   * <p>No order of execution is guaranteed if more than one class is provided - call this function
   * multiple times if this is necessary.
   *
   * @param providerClasses The class(es) of the inbound connector
   * @return DSL handle for specifying consumer calls
   */
  public ForProcessProvidersByClassDefinition<ProcessOrchestrationDefinition<M>, M> forProviders(
      final Class<? extends IntegrationScenarioDefinition>... providerClasses) {
    final ForProcessProvidersByClassDefinition<ProcessOrchestrationDefinition<M>, M> def =
        new ForProcessProvidersByClassDefinition<>(
            self(), getCompositeProcess(), Set.of(providerClasses));
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
