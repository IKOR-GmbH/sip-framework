package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;

// TODO: Missing java docs, when the inheritance structure is final add this.
public interface ConnectorDefinition<ORCHINFO_TYPE extends ConnectorOrchestrationInfo> extends Orchestratable<ORCHINFO_TYPE> {
    String getConnectorId();

    ConnectorType getConnectorType();

    String getConnectorGroupId();


}
