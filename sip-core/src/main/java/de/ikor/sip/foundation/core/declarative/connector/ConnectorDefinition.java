package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;

import java.util.Optional;

// TODO: Missing java docs, when the inheritance structure is final add this.
public interface ConnectorDefinition extends Orchestratable<ConnectorOrchestrationInfo> {
    String getConnectorId();

    ConnectorType getConnectorType();

    String getConnectorGroupId();

    String getScenarioId();

    Class<?> getRequestModelClass();

    default boolean hasResponseFlow() {
        return getResponseModelClass().isPresent();
    }

    Optional<Class<?>> getResponseModelClass();

}
