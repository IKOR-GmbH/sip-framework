package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class for specifying orchestration of complex processes */
public class ProcessOrchestrationDefinition
    extends ProcessDslBase<ProcessOrchestrationDefinition, EndOfDsl> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForProcessProviders<ProcessOrchestrationDefinition>>
      scenarioProviderDefinitions = new ArrayList<>();

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
  @SafeVarargs
  public final ForProcessProviders<ProcessOrchestrationDefinition> forProviders(
      final Class<? extends IntegrationScenarioDefinition>... providerClasses) {
    final ForProcessProviders<ProcessOrchestrationDefinition> def =
        new ForProcessProviders<>(self(), getCompositeProcess(), Set.of(providerClasses));
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
