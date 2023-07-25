package de.ikor.sip.foundation.core.declarative.composite.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * DSL class for specifying orchestration of complex processes
 *
 * @param <M> The response model type of the integration scenario
 */
public class ProcessOrchestrationDefinition<M>
    extends ProcessDslBase<ProcessOrchestrationDefinition<M>, EndOfDsl, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForProcessProvidersBase<?, ?, M>> scenarioProviderDefinitions =
      new ArrayList<>();

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param compositeProcess Composite Process
   */
  public ProcessOrchestrationDefinition(final CompositeProcessDefinition compositeProcess) {
    super(null, compositeProcess);
  }

  /**
   * Specifies process providers that should be orchestrated further by their class.
   *
   * <p>No order of execution is guaranteed if more than one class is provided - call this function
   * multiple times if this is necessary.
   *
   * @param providerClasses The class(es) of the providers
   * @return DSL handle for specifying consumer calls
   */
  public ForProcessProviders<ProcessOrchestrationDefinition<M>, M> forProviders(
      final Class<? extends IntegrationScenarioDefinition>... providerClasses) {
    final ForProcessProviders<ProcessOrchestrationDefinition<M>, M> def =
        new ForProcessProviders<>(self(), getCompositeProcess(), Set.of(providerClasses));
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
