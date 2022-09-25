package de.ikor.sip.foundation.core.framework;

import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
    private final CamelContext camelContext;
    private final String useCase;

    public UseCaseTopologyDefinition to(OutConnector outConnector) throws Exception {
        RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
        RouteDefinition routeDefinition = outConnector.configure(routeBuilder.from("sipmc:" + useCase));
        camelContext.addRoutes(routeBuilder);

        return this;
    }
}
