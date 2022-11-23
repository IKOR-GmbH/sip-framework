package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;

@CentralRouterDomainModel(responseType = String.class)
public class TestingCentralRouterDefinition extends CentralRouterDefinition {
  private static int useCaseCounter;
  public boolean isConfigured;

  @Override
  public String getScenario() {
    return "testing-use-case-" + useCaseCounter;
  }

  @Override
  public void defineTopology() throws Exception {
    isConfigured = true;
  }

  public void setupTestingState() {
    // only for testing purpose, to simulate multiple use
    ++useCaseCounter;
  }
}
