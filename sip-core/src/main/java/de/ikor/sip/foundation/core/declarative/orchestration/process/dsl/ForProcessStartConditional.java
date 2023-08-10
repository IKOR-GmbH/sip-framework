package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

/** DSL class for specifying orchestration of complex processes */
public abstract class ForProcessStartConditional<S extends ForProcessStartConditional<S, R>, R>
    extends ProcessDslBase<ForProcessStartConditional<S, R>, R>
    implements ProcessConsumerCalls<S, R> {

  @Getter(AccessLevel.PACKAGE)
  private final List<CallableWithinProcessDefinition> steps = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final List<CompositeProcessStepConditional> conditionals = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> providerClass;

  @Delegate
  @Getter(AccessLevel.PACKAGE)
  private final ForProcessProvidersDelegate<S, R> forProcessProvidersDelegate;

  /**
   * Constructor
   *
   * <p><em>For internal use only</em>
   *
   * @param compositeProcess Composite Process
   */
  ForProcessStartConditional(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> providerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.providerClass = providerClass;
    this.forProcessProvidersDelegate =
        new ForProcessProvidersDelegate(steps, self(), getDslReturnDefinition(), providerClass);
  }

  public CallNestedCondition<S>.ProcessBranch<CallNestedCondition<S>> ifCase(
      final CompositeProcessStepConditional predicate) {
    final CallNestedCondition<S> def =
        new CallNestedCondition(self(), getCompositeProcess(), providerClass);
    steps.add(def);
    conditionals.add(predicate);
    return def.elseIfCase(predicate);
  }
}
