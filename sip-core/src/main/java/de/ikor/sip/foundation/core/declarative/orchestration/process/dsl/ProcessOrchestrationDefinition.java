package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class for specifying orchestration of complex processes */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ProcessOrchestrationDefinition
    extends ProcessDslBase<ProcessOrchestrationDefinition, EndOfDsl> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForProcessStartConditionalImpl<?>> scenarioProviderDefinitions =
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
   * @param providerClass The class of the provider
   * @return DSL handle for specifying consumer calls
   */
  public final ForProcessStartConditionalImpl<ProcessOrchestrationDefinition> forProvider(
      final Class<? extends IntegrationScenarioDefinition> providerClass) {
    final ForProcessStartConditionalImpl<ProcessOrchestrationDefinition> def =
        new ForProcessStartConditionalImpl(self(), getCompositeProcess(), providerClass);
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
