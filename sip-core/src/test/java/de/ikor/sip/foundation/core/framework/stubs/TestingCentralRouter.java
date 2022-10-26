package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.CentralRouter;

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

  @Override
  public void configureOnCentralRouterLevel() {

  }

  public void setupTestingState() {
    // only for testing purpose, to simulate multiple use
    ++useCaseCounter;
  }
}
