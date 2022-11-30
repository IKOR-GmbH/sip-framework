package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import lombok.AllArgsConstructor;
import org.apache.camel.model.RouteDefinition;

@AllArgsConstructor
public class OutConnectorStub extends OutConnector {

    private OutEndpoint outEndpoint;

    @Override
    public void configure(RouteDefinition route) {
        route
            .to(outEndpoint)
            .to("log:messageOut");
    }

    @Override
    public String getName() {
        return "OutConnectorStub";
    }
}
