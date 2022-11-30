package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.ConfigInConnector;
import de.ikor.sip.foundation.core.framework.stubs.NoConfigOutConnector;

@IntegrationScenario(name = "no-config-scenario")
public class NoConfigurationTestingCentralRouter extends CentralRouterDefinition {

  @Override
  public void defineTopology() throws Exception {
    input(ConfigInConnector.withSedaChannel("no-config")).parallelOutput(new NoConfigOutConnector("no-config").outEndpointId("no-config"));
  }

  @Override
  public void defineConfiguration() {
  }

  @Override
  public void configureOnException() {}
}
