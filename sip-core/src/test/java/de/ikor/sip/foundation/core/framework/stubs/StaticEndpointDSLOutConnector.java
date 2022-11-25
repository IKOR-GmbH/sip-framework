package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.file;

public class StaticEndpointDSLOutConnector extends OutConnectorDefinition {

    @Override
    public void configure(RouteDefinition route) {
        route.to(OutEndpoint.instance(file("temp/out").fileName("testfile.txt"), "staticendpointdsl-id"));
    }
}
