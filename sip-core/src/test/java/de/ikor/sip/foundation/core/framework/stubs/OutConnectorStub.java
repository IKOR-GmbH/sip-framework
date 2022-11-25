package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import lombok.AllArgsConstructor;
import org.apache.camel.model.RouteDefinition;

@AllArgsConstructor
public class OutConnectorStub extends OutConnectorDefinition {

    private OutEndpoint outEndpoint;

    @Override
    public void configure(RouteDefinition route) {
        route
            .to(outEndpoint)
            .to("log:messageOut");
    }
}
