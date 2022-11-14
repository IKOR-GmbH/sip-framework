package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;

@CentralRouterDomainModel(requestType = WrongTypeRouterDefinition.class)
public class WrongTypeRouterDefinition extends CentralRouterDefinition {
    @Override
    public String getScenario() {
        return "WrongTypeRouter";
    }

    @Override
    public void defineTopology() throws Exception {
        SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-7");
        SimpleOutConnector outConnector = new SimpleOutConnector();
        input(inConnector)
                .sequencedOutput(outConnector);
    }
}
