package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepConditional;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

public class CallNestedCondition<R>
        extends ProcessDslBase<CallNestedCondition<R>, R>
        implements CallableWithinProcessDefinition{

    @Getter(AccessLevel.PACKAGE)
    private final List<ProcessBranchStatements<R>> conditionalStatements = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private final List<ProcessBranchStatements<R>> unconditionalStatements = new ArrayList<>();

    private final CompositeProcessDefinition processDefinition;
    private final Class clazz;

    CallNestedCondition(
            R dslReturnDefinition,
            CompositeProcessDefinition compositeProcess,
            Class providerScenarioClass) {
        super(dslReturnDefinition, compositeProcess);
        this.processDefinition = compositeProcess;
        this.clazz = providerScenarioClass;
    }


    ProcessBranch<CallNestedCondition<R>> elseIfCase(
            final CompositeProcessStepConditional predicate) {
        final var branch = new ProcessBranchStatements(predicate, new ArrayList<>());
        conditionalStatements.add(branch);
        return new ProcessBranch(branch.statements, self(), processDefinition, clazz);
    }


    ProcessBranch<R> elseCase() {
        return new ProcessBranch(unconditionalStatements, getDslReturnDefinition(), processDefinition, clazz);
    }


    R endCases() {
        return getDslReturnDefinition();
    }

    record ProcessBranchStatements<M>(
            CompositeProcessStepConditional predicate,
            List<CallProcessConsumerBase> statements) {}

    public final class ProcessBranch<I> extends ProcessDslBase<CallNestedCondition<I>.ProcessBranch<I>, I>
        implements ProcessConsumerCalls<ProcessBranch<I>, I> {

        @Delegate
        private final ForProcessProvidersDelegate<ProcessBranch<I>, I> delegate;

        ProcessBranch(
                final List<ProcessBranchStatements> statementsList,
                final I dslReturnDefinition,
                final CompositeProcessDefinition processDefinition,
                Class<? extends IntegrationScenarioDefinition> consumerClass) {
            super(dslReturnDefinition, processDefinition);
            delegate =
                    new ForProcessProvidersDelegate(
                            dslReturnDefinition,
                            processDefinition,
                            consumerClass,
                            self());
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
