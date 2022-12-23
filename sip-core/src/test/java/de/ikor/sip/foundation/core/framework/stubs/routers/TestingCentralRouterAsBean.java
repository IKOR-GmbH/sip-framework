package de.ikor.sip.foundation.core.framework.stubs.routers;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;

@IntegrationScenario(name = "testing-use-case-", responseType = String.class)
public class TestingCentralRouterAsBean extends CentralRouter {
  public boolean isConfigured;

  @Override
  public void defineTopology() {
    isConfigured = true;
    input(SimpleInConnector.withUri("direct:dummyMockBean"));
  }
}
