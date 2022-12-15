package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EndpointDSLInConnector extends InConnector {

  private String endpointPath;
  private String endpointId;

  @Override
  public String getName() {
    return "EndpointDslInConnector";
  }

  @Override
  public void configure() {
    from(InEndpoint.instance(endpointDsl().direct(endpointPath), endpointId));
  }
}
