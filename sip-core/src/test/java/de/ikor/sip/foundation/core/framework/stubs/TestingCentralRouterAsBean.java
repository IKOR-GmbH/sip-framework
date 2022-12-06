package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name = "testing-use-case-", responseType = String.class)
public class TestingCentralRouterAsBean extends CentralRouter {

    private static int useCaseCounter;
    public boolean isConfigured;

    @Override
    public void defineTopology() throws Exception {
        isConfigured = true;
        input(SimpleInConnector.withUri("direct:dummyMockBean"));
    }

    public void setupTestingState() {
        // only for testing purpose, to simulate multiple use
        ++useCaseCounter;
    }

    public String getScenario() {
        return this.getClass().getAnnotation(IntegrationScenario.class).name();
    }
}
