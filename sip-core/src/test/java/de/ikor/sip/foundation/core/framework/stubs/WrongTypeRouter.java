package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;

@IntegrationScenario(name = "WrongTypeRouter", requestType = WrongTypeRouter.class)
public class WrongTypeRouter extends CentralRouter {

  @Override
  public void defineTopology() {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-7");
    SimpleOutConnector outConnector = new SimpleOutConnector();
    input(inConnector).sequencedOutput(outConnector);
  }
}
