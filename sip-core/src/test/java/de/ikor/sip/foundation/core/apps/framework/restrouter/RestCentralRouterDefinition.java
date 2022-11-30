package de.ikor.sip.foundation.core.apps.framework.restrouter;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.AppendStringOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.RestInConnector;

@IntegrationScenario(name = "rest-scenario", responseType = String.class)
public class RestCentralRouterDefinition extends CentralRouterDefinition {

  @Override
  public void defineTopology() throws Exception {
    input(new RestInConnector("/hello-append")).sequencedOutput(new AppendStringOutConnector());
  }

  @Override
  public void configureOnException() {}
}
