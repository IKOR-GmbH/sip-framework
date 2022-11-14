package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.Getter;
import org.apache.camel.Endpoint;

public class InEndpoint {

  @Getter private String uri;
  @Getter private String id;
  Endpoint someEp;

  public static InEndpoint instance(String uri, String id) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    CentralEndpointsRegister.put(id, inEndpoint);
    return CentralEndpointsRegister.getInEndpoint(id);
  }

  protected InEndpoint(String uri, String id) {
    this.uri = uri;
    this.id = id;
  }
}
