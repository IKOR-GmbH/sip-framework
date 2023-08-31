package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

public final class CallNestedCondition<R> extends ProcessDslBase<CallNestedCondition<R>, R>
    implements CallableWithinProcessDefinition {

  @Getter(AccessLevel.PACKAGE)
  private final List<ProcessBranchStatements> conditionalStatements = new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private final List<CallableWithinProcessDefinition> unconditionalStatements = new ArrayList<>();

  private final CompositeProcessDefinition processDefinition;

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<Object>> stepResultCloner = Optional.empty();

  CallNestedCondition(R dslReturnDefinition, CompositeProcessDefinition compositeProcess) {
    super(dslReturnDefinition, compositeProcess);
    this.processDefinition = compositeProcess;
  }

  ProcessBranch<CallNestedCondition<R>> elseIfCase(
      final CompositeProcessStepConditional predicate) {
    final var branch = new ProcessBranchStatements(predicate, new ArrayList<>());
    conditionalStatements.add(branch);
    return new ProcessBranch(branch.statements, self(), processDefinition);
  }

  ProcessBranch<R> elseCase() {
    return new ProcessBranch(unconditionalStatements, getDslReturnDefinition(), processDefinition);
  }

  R endCases() {
    return getDslReturnDefinition();
  }

  public record ProcessBranchStatements(
      CompositeProcessStepConditional predicate,
      List<CallableWithinProcessDefinition> statements) {}

  public final class ProcessBranch<I>
      extends ProcessDslBase<CallNestedCondition<I>.ProcessBranch<I>, I>
      implements ProcessConsumerCalls<ProcessBranch<I>, I> {

    @Delegate private final ForProcessProvidersDelegate<ProcessBranch<I>, I> delegate;

    ProcessBranch(
        final List<ProcessBranchStatements> statementsList,
        final I dslReturnDefinition,
        final CompositeProcessDefinition processDefinition) {
      super(dslReturnDefinition, processDefinition);
      delegate = new ForProcessProvidersDelegate(statementsList, self(), getDslReturnDefinition());
    }

    public ProcessBranch<CallNestedCondition<R>> elseIfCase(
        final CompositeProcessStepConditional predicate) {
      return CallNestedCondition.this.elseIfCase(predicate);
    }

    public ProcessBranch<R> elseCase() {
      return CallNestedCondition.this.elseCase();
    }

    public R endCases() {
      return CallNestedCondition.this.endCases();
    }
  }
}
