package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name =  "WrongTypeRouter", requestType = WrongTypeRouterDefinition.class)
public class WrongTypeRouterDefinition extends CentralRouterDefinition {

  @Override
  public void defineTopology() throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-7");
    SimpleOutConnector outConnector = new SimpleOutConnector();
    input(inConnector).sequencedOutput(outConnector);
  }
}
