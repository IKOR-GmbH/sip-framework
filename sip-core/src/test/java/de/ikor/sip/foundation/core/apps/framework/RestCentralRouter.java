package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.AppendStringOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.RestInConnector;

public class RestCentralRouter extends CentralRouter {
  @Override
  public String getScenario() {
    return "rest-scenario";
  }

  @Override
  public void configure() throws Exception {
    input(new RestInConnector()).output(new AppendStringOutConnector());
  }

  @Override
  public void configureOnException() {}
}
