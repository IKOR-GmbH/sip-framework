package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

public class EndpointDSLInConnector extends InConnectorDefinition {

    @Override
    public String getName() {
        return "EndpointDslInConnector";
    }

    @Override
    public void configure() {
        from(InEndpoint.instance(endpointDsl().direct("endpointdsl-direct"), "endpointdsl-id"));
    }
}
