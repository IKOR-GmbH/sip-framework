package de.ikor.sip.foundation.core.framework.stubs.routers;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.ScopeAppendOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.ScopeBeanInConnector;
import org.apache.camel.converter.stream.InputStreamCache;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationScenario(
    name = "bean-scenario",
    responseType = String.class,
    requestType = InputStreamCache.class)
public class ScopeBeanCentralRouter extends CentralRouter {
  @Autowired private ScopeAppendOutConnector appendStringOutConnector;
  @Autowired private ScopeBeanInConnector restInConnector;

  @Override
  public void defineTopology() {
    input(restInConnector).sequencedOutput(appendStringOutConnector);
  }

  @Override
  public void configureOnException() {}
}
