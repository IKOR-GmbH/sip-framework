package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name = "testing-use-case-", responseType = String.class)
public class TestingCentralRouter extends CentralRouter {
  private static int useCaseCounter;
  public boolean isConfigured;

  @Override
  public void defineTopology() throws Exception {
    isConfigured = true;
  }

  public void setupTestingState() {
    // only for testing purpose, to simulate multiple use
    ++useCaseCounter;
  }

  public String getScenario() {
    return this.getClass().getAnnotation(IntegrationScenario.class).name();
  }
}
