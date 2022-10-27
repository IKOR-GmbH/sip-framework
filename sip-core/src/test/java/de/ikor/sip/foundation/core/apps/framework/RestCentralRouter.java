package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.framework.CentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.AppendStringOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.RestInConnector;

public class RestCentralRouter extends CentralRouter {
  @Override
  public String getScenario() {
    return "rest-scenario";
  }

  @Override
  public void configure() throws Exception {
    from(new RestInConnector()).to(new AppendStringOutConnector());
  }

  @Override
  public void configureOnCentralRouterLevel() {

  }
}
