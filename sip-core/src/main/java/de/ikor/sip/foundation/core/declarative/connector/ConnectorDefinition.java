package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import org.apache.camel.model.OptionalIdentifiedDefinition;

// TODO: Missing java docs, when the inheritance structure is final add this.
public interface ConnectorDefinition<ORCHINFO_TYPE extends ConnectorOrchestrationInfo, DEFINITION_TYPE extends OptionalIdentifiedDefinition<DEFINITION_TYPE>> extends Orchestratable<ORCHINFO_TYPE> {
    String getConnectorId();

    ConnectorType getConnectorType();

    String getConnectorGroupId();

    Class<? extends DEFINITION_TYPE> getEndpointDefinitionTypeClass();

}
