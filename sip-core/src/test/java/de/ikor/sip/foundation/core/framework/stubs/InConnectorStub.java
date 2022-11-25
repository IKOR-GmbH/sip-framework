package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InConnectorStub extends InConnectorDefinition {

    private InEndpoint inEndpoint;

    @Override
    public String getName() {
        return "BasicInConnector";
    }

    @Override
    public void configure() {
        from(inEndpoint)
                .to("log:messageIn");
    }
}
