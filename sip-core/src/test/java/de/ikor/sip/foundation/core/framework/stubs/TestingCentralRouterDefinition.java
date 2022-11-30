package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name = "testing-use-case-", responseType = String.class)
public class TestingCentralRouterDefinition extends CentralRouterDefinition {
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
