package de.ikor.sip.foundation.core.framework.stubs.routers;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name = "testing-use-case-", responseType = String.class)
public class TestingCentralRouter extends CentralRouter {

  @Override
  public void defineTopology() {}

  public String getScenario() {
    return this.getClass().getAnnotation(IntegrationScenario.class).name();
  }
}
