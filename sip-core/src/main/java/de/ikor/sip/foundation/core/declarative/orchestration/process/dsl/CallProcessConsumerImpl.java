package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

public final class CallProcessConsumerImpl<R>
        extends CallProcessConsumerBase<CallProcessConsumerImpl<R>, R>{

    @Getter(AccessLevel.PACKAGE)
    private final Class<? extends IntegrationScenarioDefinition> consumerClass;

    CallProcessConsumerImpl(
            R dslReturnDefinition,
            CompositeProcessDefinition compositeProcess,
            Class<? extends IntegrationScenarioDefinition> consumerClass) {
        super(dslReturnDefinition, compositeProcess, consumerClass);
        this.consumerClass = consumerClass;
    }
}
