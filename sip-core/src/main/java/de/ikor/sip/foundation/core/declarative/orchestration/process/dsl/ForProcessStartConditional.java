package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
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
      final R dslReturnDefinition, final CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition, compositeProcess);
    this.forProcessProvidersDelegate =
        new ForProcessProvidersDelegate(steps, self(), getDslReturnDefinition());
  }

  public CallNestedCondition<S>.ProcessBranch<CallNestedCondition<S>> ifCase(
      final CompositeProcessStepConditional predicate) {
    final CallNestedCondition<S> def = new CallNestedCondition(self(), getCompositeProcess());
    steps.add(def);
    conditionals.add(predicate);
    return def.elseIfCase(predicate);
  }
}
