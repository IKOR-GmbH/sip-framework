package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
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
  private final Class providerScenarioClass;

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepRequestExtractor> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepResponseConsumer> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<Object>> stepResultCloner = Optional.empty();

  CallNestedCondition(
      R dslReturnDefinition,
      CompositeProcessDefinition compositeProcess,
      Class providerScenarioClass) {
    super(dslReturnDefinition, compositeProcess);
    this.processDefinition = compositeProcess;
    this.providerScenarioClass = providerScenarioClass;
  }

  ProcessBranch<CallNestedCondition<R>> elseIfCase(
      final CompositeProcessStepConditional predicate) {
    final var branch = new ProcessBranchStatements(predicate, new ArrayList<>());
    conditionalStatements.add(branch);
    return new ProcessBranch(branch.statements, self(), processDefinition, providerScenarioClass);
  }

  ProcessBranch<R> elseCase() {
    return new ProcessBranch(
        unconditionalStatements,
        getDslReturnDefinition(),
        processDefinition,
        providerScenarioClass);
  }

  R endCases() {
    return getDslReturnDefinition();
  }

  public record ProcessBranchStatements(
      CompositeProcessStepConditional predicate, List<CallProcessConsumerBase> statements) {}

  public final class ProcessBranch<I>
      extends ProcessDslBase<CallNestedCondition<I>.ProcessBranch<I>, I>
      implements ProcessConsumerCalls<ProcessBranch<I>, I> {

    @Delegate private final ForProcessProvidersDelegate<ProcessBranch<I>, I> delegate;

    ProcessBranch(
        final List<ProcessBranchStatements> statementsList,
        final I dslReturnDefinition,
        final CompositeProcessDefinition processDefinition,
        Class<? extends IntegrationScenarioDefinition> consumerClass) {
      super(dslReturnDefinition, processDefinition);
      delegate =
          new ForProcessProvidersDelegate(
              statementsList, self(), getDslReturnDefinition(), consumerClass);
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
