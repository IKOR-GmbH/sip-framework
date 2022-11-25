package de.ikor.sip.foundation.core.apps.framework.restrouter;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;
import de.ikor.sip.foundation.core.framework.stubs.AppendStringOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.RestInConnector;
import de.ikor.sip.foundation.core.framework.stubs.ScopeAppendOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.ScopeBeanInConnector;
import org.apache.camel.converter.stream.InputStreamCache;
import org.springframework.beans.factory.annotation.Autowired;

@CentralRouterDomainModel(responseType = String.class, requestType = InputStreamCache.class)
public class ScopeBeanCentralRouterDefinition extends CentralRouterDefinition {

  @Autowired
  private ScopeAppendOutConnector appendStringOutConnector;
  @Autowired
  private ScopeBeanInConnector restInConnector;

  @Override
  public String getScenario() {
    return "bean-scenario";
  }

  @Override
  public void defineTopology() throws Exception {
    input(restInConnector).sequencedOutput(appendStringOutConnector);
  }

  @Override
  public void configureOnException() {}
}
