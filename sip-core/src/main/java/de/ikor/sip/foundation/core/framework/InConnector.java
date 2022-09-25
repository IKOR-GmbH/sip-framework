package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.CentralRouter.anonymousDummyRouteBuilder;

public abstract class InConnector extends Connector {
    @Getter
    private RouteDefinition connectorDefinition;
    @Getter
    private final RouteBuilder routeBuilder = anonymousDummyRouteBuilder();
    public abstract void configure();

    protected RouteDefinition from(InEndpoint inEndpoint) {
        return connectorDefinition = routeBuilder.from(inEndpoint.getUri());
    }

    public String getEndpointUri() {
        return connectorDefinition.getEndpointUrl();
    }
}
