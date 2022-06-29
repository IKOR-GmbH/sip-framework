package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.RestRouteProducer;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl.CxfRouteProducer;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Endpoint;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RouteProducerFactory {

    private final CxfRouteProducer soapRouteProducer;
    private final RestRouteProducer restRouteProducer;

    public Optional<RouteProducer> resolveRouteProducer(Endpoint endpoint) {
        if (endpoint instanceof CxfEndpoint) {
            return Optional.of(soapRouteProducer);
        }
        if (endpoint instanceof RestEndpoint) {
            return Optional.of(restRouteProducer);
        }
        return Optional.empty();
    }
}
