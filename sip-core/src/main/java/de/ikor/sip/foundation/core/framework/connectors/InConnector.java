package de.ikor.sip.foundation.core.framework.connectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteBuilder;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.builder.RouteConfigurationBuilder;

public class InConnector {
  @Getter private final InConnectorDefinition connector;
  @Getter private RouteBuilder routeBuilder;
  @Setter private RouteConfigurationBuilder configuration;

  public InConnector(InConnectorDefinition connector) {
    this.connector = connector;
  }

  public void configure() {
    connector.configure();
    routeBuilder.getRouteCollection().route(connector.getRouteDefinition());
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

  public void configureOnException() {
    connector.configureOnException();
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    return connector.onException(exceptions);
  }

  public void setDefinition() {
    routeBuilder = anonymousDummyRouteBuilder(configuration);
    connector.setRouteBuilder(routeBuilder);
    connector.setDefinition();
  }
}
