package de.ikor.sip.foundation.core.framework.endpoints;

import org.apache.camel.model.rest.RestDefinition;

public class RestInEndpoint extends InEndpoint {

  private final RestDefinition restDefinition;

  public static RestInEndpoint instance(String uri, String id) {
    RestInEndpoint inEndpoint = new RestInEndpoint(uri, id);
    CentralEndpointsRegister.put(id, inEndpoint);
    return inEndpoint;
  }

  protected RestInEndpoint(String uri, String id) {
    super(uri, id);
    this.restDefinition = new RestDefinition();
    this.restDefinition.setPath(uri);
  }

  public RestDefinition definition() {
    return restDefinition;
  }
}
