package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.CentralRouter;

public class TestingCentralRouter extends CentralRouter {
    private int useCaseCounter;
    public boolean isConfigured;

    @Override
    public String getUseCase() {
        return "testing-use-case-" + useCaseCounter;
    }

    @Override
    public void configure() throws Exception {
        isConfigured = true;
        //TODO don't do this
    }

    public void setupTestingState() {
//        isConfigured = false;
        // only for testing purpose, to simulate multiple use
        ++useCaseCounter;
    }
}
