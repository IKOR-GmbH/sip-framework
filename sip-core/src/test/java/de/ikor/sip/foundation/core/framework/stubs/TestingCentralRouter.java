package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;

public class TestingCentralRouter extends CentralRouter {
  private static int useCaseCounter;
  public boolean isConfigured;

  @Override
  public String getScenario() {
    return "testing-use-case-" + useCaseCounter;
  }

  @Override
  public void configure() throws Exception {
    isConfigured = true;
  }

  public void setupTestingState() {
    // only for testing purpose, to simulate multiple use
    ++useCaseCounter;
  }
}
