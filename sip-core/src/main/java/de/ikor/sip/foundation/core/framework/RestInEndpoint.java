package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestDefinition;

public class RestInEndpoint {
  @Getter private String uri;
  @Getter private String id;
  @Getter private RouteBuilder routeBuilder;
  private RestDefinition restDefinition;

  public static RestInEndpoint instance(String uri, String id, RouteBuilder routeBuilder) {

    RestInEndpoint inEndpoint = new RestInEndpoint(uri, id, routeBuilder);
    CentralEndpointsRegister.put(id, inEndpoint);
    return CentralEndpointsRegister.getRestInEndpoint(id);
  }

  protected RestInEndpoint(String uri, String id, RouteBuilder routeBuilder) {
    this.routeBuilder = routeBuilder;
    this.restDefinition = this.routeBuilder.rest(uri);
    this.uri = uri;
    this.id = id;
  }

  public RestDefinition rest() {
    return restDefinition;
  }
}
