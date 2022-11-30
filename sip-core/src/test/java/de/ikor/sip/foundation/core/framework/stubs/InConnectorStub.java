package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@CentralRouterDomainModel(requestType = InEndpointDomain.class)// TODO this should be some central domain
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
