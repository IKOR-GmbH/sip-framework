package de.ikor.sip.foundation.core.framework.connectors;

import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;

public abstract class Connector {
  public abstract String getName();

  @Getter protected RouteBuilder routeBuilder;
  private EndpointRouteBuilder endpointRouteBuilder;

  public void configureOnException() {}

  public EndpointRouteBuilder endpointDsl() {
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

  void initBuilders(RouteConfigurationBuilder configurationBuilder) {
    this.routeBuilder = StaticRouteBuilderHelper.configuredRouteBuilder(configurationBuilder);
    this.endpointRouteBuilder = anonymousDummyEndpointRouteBuilder();
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    OnExceptionDefinition last = null;

    for (Class<? extends Throwable> ex : exceptions) {
      last = (last == null ? this.routeBuilder.onException(ex) : last.onException(ex));
    }
    return last;
  }
}
