package de.ikor.sip.foundation.core.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;

public class MulticastDefinition extends org.apache.camel.model.MulticastDefinition {
    private final String useCase;
    private org.apache.camel.model.MulticastDefinition currentDefinition;

    public MulticastDefinition(String useCase, CamelContext camelContext) {
        setCamelContext(camelContext);
        this.useCase = useCase;
    }

    public MulticastDefinition(String useCase, org.apache.camel.model.MulticastDefinition multicastDefinition, CamelContext camelContext) {
        this.useCase = useCase;
        setCamelContext(camelContext);
        this.currentDefinition = multicastDefinition;
    }

    public UseCaseTopologyDefinition to(OutConnector outConnector) {
        return new UseCaseTopologyDefinition(getCamelContext(), useCase);
    }
}
