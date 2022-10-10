package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.CentralEndpointsRegister.getInEndpointUri;
import static de.ikor.sip.foundation.core.framework.CentralRouter.anonymousDummyRouteBuilder;

public abstract class InConnector extends Connector {
    @Getter
    private RouteBuilder routeBuilder;
    public abstract void configure();

    protected RouteDefinition from(InEndpoint inEndpoint) {
        routeBuilder = anonymousDummyRouteBuilder();
        return routeBuilder.from(getInEndpointUri(inEndpoint.getId()));
    }

    public String getEndpointUri() {
        return getConnectorDefinition().getEndpointUrl();
    }

    public RouteDefinition getConnectorDefinition() {
        return routeBuilder.getRouteCollection().getRoutes().get(0);
    }
}
