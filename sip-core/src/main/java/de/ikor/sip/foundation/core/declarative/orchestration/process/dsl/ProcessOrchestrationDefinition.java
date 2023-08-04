package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** DSL class for specifying orchestration of complex processes */
public class ProcessOrchestrationDefinition
    extends ProcessDslBase<ProcessOrchestrationDefinition, EndOfDsl> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForProcessStartCondition<ProcessOrchestrationDefinition>>
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
   * @param providerClass The class of the provider
   * @return DSL handle for specifying consumer calls
   */
  public final ForProcessStartCondition<ProcessOrchestrationDefinition> forProvider(
      final Class<? extends IntegrationScenarioDefinition> providerClass) {
    final ForProcessStartCondition<ProcessOrchestrationDefinition> def =
        new ForProcessStartCondition(self(), getCompositeProcess(), providerClass);
    scenarioProviderDefinitions.add(def);
    return def;
  }


}
