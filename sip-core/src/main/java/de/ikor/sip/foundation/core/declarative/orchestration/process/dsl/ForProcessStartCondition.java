package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

/** DSL class for specifying orchestration of complex processes */
public class ForProcessStartCondition<R>
         extends ProcessDslBase<ForProcessStartCondition<R>, R> {


  @Getter(AccessLevel.PACKAGE)
  private final List<CallNestedCondition<CompositeProcessStepConditional>> conditionals = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> providerClass;

  @Delegate
  @Getter(AccessLevel.PACKAGE)
  private final ForProcessProviders<R> forProcessProviders;

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param compositeProcess Composite Process
   */
  public ForProcessStartCondition(
          final R dslReturnDefinition,
          final CompositeProcessDefinition compositeProcess,
          final Class<? extends IntegrationScenarioDefinition> providerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClass = providerClass;
    this.forProcessProviders = new ForProcessProviders<R>(
            getDslReturnDefinition(),
            compositeProcess,
            providerClass
    );
  }

  public CallNestedCondition<CompositeProcessStepConditional>
          .ProcessBranch<CallNestedCondition<CompositeProcessStepConditional>> ifCase(
          final CompositeProcessStepConditional predicate) {
    final CallNestedCondition<CompositeProcessStepConditional> def =
            new CallNestedCondition<>(predicate, getCompositeProcess(), providerClass);
    conditionals.add(def);
    return def.elseIfCase(predicate);
  }
}
