package de.ikor.sip.foundation.core.framework.connectors;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

public abstract class Connector {

  private EndpointRouteBuilder endpointRouteBuilder;

  public abstract String getName();
  public void configureOnException() {

  };

  public EndpointRouteBuilder endpointDsl() {
    if (endpointRouteBuilder == null) {
      return anonymousDummyEndpointRouteBuilder();
    }
    return endpointRouteBuilder;
  }


  private EndpointRouteBuilder anonymousDummyEndpointRouteBuilder() {
    return new EndpointRouteBuilder() {
      @Override
      public void configure() {
        // no need for implementation; used for building routes
      }
    };
  }
}
