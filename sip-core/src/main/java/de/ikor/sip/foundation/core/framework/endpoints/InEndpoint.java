package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.Getter;

public class InEndpoint {

  @Getter private String uri;
  @Getter private String id;

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
