package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

public class EndpointDSLInConnector extends InConnector {

  @Override
  public String getName() {
    return "EndpointDslInConnector";
  }

  @Override
  public void configure() {
    from(InEndpoint.instance(endpointDsl().direct("endpointdsl-direct"), "endpointdsl-id"));
  }
}
