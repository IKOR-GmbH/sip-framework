package de.ikor.sip.foundation.core.framework.connectors;

import lombok.RequiredArgsConstructor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteBuilder;

@RequiredArgsConstructor
public class InConnector {
  private final InConnectorDefinition connector;
  private RouteBuilder routeBuilder;

  public void configure() {
    connector.configure();
    routeBuilder.getRouteCollection().route(connector.getRouteDefinition());
  }

  public RouteDefinition getConnectorTestingRouteDefinition() {
    return routeBuilder.getRouteCollection().getRoutes().get(0);
  }

  // TODO: Assumption here is that the first route is the "regular" one and the second
  // is the testing one. This hardcoded .get(0) and .get(1) should be refactored
  public RouteDefinition getConnectorRouteDefinition() {
    return routeBuilder.getRouteCollection().getRoutes().get(0);
  }

  public String getName() {
    return connector.getName();
  }

  public void handleResponse(RouteDefinition connectorTestingRouteDefinition) {
    connector.handleResponse(connectorTestingRouteDefinition);
  }

  public RoutesBuilder getRouteBuilder() {
    return routeBuilder;
  }

  public void configureOnException() {
    connector.configureOnException();
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    return connector.onException(exceptions);
  }

  public void setDefinition() {
    routeBuilder = anonymousDummyRouteBuilder();
    connector.setDefinition();
  }

  private RouteBuilder getRouteBuilderInstance() {
    if (routeBuilder == null) {
      return anonymousDummyRouteBuilder();
    }
    return routeBuilder;
  }
}
