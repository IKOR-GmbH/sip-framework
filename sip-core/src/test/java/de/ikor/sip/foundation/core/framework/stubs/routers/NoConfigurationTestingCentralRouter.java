package de.ikor.sip.foundation.core.framework.stubs.routers;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.ConfigInConnector;
import de.ikor.sip.foundation.core.framework.stubs.NoConfigOutConnector;

@IntegrationScenario(name = "no-config-scenario")
public class NoConfigurationTestingCentralRouter extends CentralRouter {

  @Override
  public void defineTopology() {
    input(ConfigInConnector.withSedaChannel("no-config"))
        .parallelOutput(new NoConfigOutConnector("no-config").withId("no-config"));
  }

  @Override
  public void defineConfiguration() {}

  @Override
  public void configureOnException() {}
}
