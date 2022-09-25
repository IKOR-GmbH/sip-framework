package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.OutConnector;
import de.ikor.sip.foundation.core.framework.OutEndpoint;
import lombok.NoArgsConstructor;
import org.apache.camel.model.RouteDefinition;
@NoArgsConstructor
public class SimpleOutConnector extends OutConnector {
    private String endpointId = "endpoint-id";
    public SimpleOutConnector(String outEndpointId) {
        endpointId = outEndpointId;
    }
    @Override
    public RouteDefinition configure(RouteDefinition route) {
        return route.to(OutEndpoint.instance("log:message", endpointId)).id("log-message-endpoint");
    }
}
