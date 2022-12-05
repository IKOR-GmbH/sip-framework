package de.ikor.sip.foundation.core.framework.stubs;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.file;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import lombok.AllArgsConstructor;
import org.apache.camel.model.RouteDefinition;

@AllArgsConstructor
public class StaticEndpointDSLOutConnector extends OutConnector {

  private String endpointPath;
  private String endpointId;

  @Override
  public String getName() {
    return endpointId;
  }

  @Override
  public void configure(RouteDefinition route) {
    route.to(
        OutEndpoint.instance(direct(endpointPath), endpointId));
  }
}
