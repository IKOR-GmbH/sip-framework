package de.ikor.sip.foundation.core.framework;

import static de.ikor.sip.foundation.core.framework.CentralEndpointsRegister.getInEndpointUri;
import static de.ikor.sip.foundation.core.framework.CentralRouter.anonymousDummyRouteBuilder;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;

public abstract class InConnector extends Connector {
  @Getter private RouteBuilder routeBuilder;

  public abstract void configure();

  public abstract void configureOnConnectorLevel();

  protected RouteDefinition from(InEndpoint inEndpoint) {
    routeBuilder = getRouteBuilderInstance();
    return routeBuilder.from(getInEndpointUri(inEndpoint.getId()));
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    routeBuilder = getRouteBuilderInstance();
    OnExceptionDefinition last = null;

    for (Class<? extends Throwable> ex : exceptions) {
      last = (last == null ? this.routeBuilder.onException(ex) : last.onException(ex));
    }
    return last;
  }

  protected void createNewRouteBuilder() {
    routeBuilder = anonymousDummyRouteBuilder();
  }

  private RouteBuilder getRouteBuilderInstance() {
    if (routeBuilder == null) {
      return anonymousDummyRouteBuilder();
    }
    return routeBuilder;
  }

  public String getEndpointUri() {
    return getConnectorDefinition().getEndpointUrl();
  }

  public RouteDefinition getConnectorDefinition() {
    return routeBuilder.getRouteCollection().getRoutes().get(0);
  }
}
