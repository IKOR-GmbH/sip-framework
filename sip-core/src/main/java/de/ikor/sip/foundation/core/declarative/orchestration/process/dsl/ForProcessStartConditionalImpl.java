package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.Getter;

public class ForProcessStartConditionalImpl<R>
        extends ForProcessStartConditional<ForProcessStartConditionalImpl<R>, R> {

    @Getter
    private final Class<? extends IntegrationScenarioDefinition> providerClass;
    /**
     * Constructor
     *
     * <p><em>For internal use only</em>
     *
     * @param dslReturnDefinition
     * @param compositeProcess    Composite Process
     * @param providerClass
     */
    public ForProcessStartConditionalImpl(
            R dslReturnDefinition,
            CompositeProcessDefinition compositeProcess,
            Class<? extends IntegrationScenarioDefinition> providerClass) {
        super(dslReturnDefinition, compositeProcess, providerClass);
        this.providerClass = providerClass;
    }


}
