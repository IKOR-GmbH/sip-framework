package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ConnectorBase
        implements ConnectorDefinition, Orchestrator<ConnectorOrchestrationInfo> {

    @Getter
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Delegate
    private final Orchestrator<ConnectorOrchestrationInfo> modelTransformationOrchestrator = defineTransformationOrchestrator();

    @Override
    public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
        return this;
    }

    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
        return ConnectorOrchestrator.forConnector(this);
    }

}
