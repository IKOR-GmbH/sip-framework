package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.Getter;
import org.apache.camel.builder.EndpointConsumerBuilder;

public class InEndpoint {

  @Getter private String uri;
  @Getter private String id;

  public static InEndpoint instance(String uri, String id) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    CentralEndpointsRegister.put(id, inEndpoint);
    return inEndpoint;
  }

  public static InEndpoint instance(EndpointConsumerBuilder endpointDslDefinition, String id) {
    return instance(endpointDslDefinition.getUri(), id);
  }

  protected InEndpoint(String uri, String id) {
    this.uri = uri;
    this.id = id;
  }
}
