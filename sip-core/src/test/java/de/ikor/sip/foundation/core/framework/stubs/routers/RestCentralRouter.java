package de.ikor.sip.foundation.core.framework.stubs.routers;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.AppendStringOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.RestInConnector;

@IntegrationScenario(name = "rest-scenario", responseType = String.class)
public class RestCentralRouter extends CentralRouter {

  @Override
  public void defineTopology() {
    input(new RestInConnector("/hello-append")).sequencedOutput(new AppendStringOutConnector());
  }

  @Override
  public void configureOnException() {}
}
