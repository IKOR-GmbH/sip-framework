package de.ikor.sip.foundation.core.framework.connectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteBuilder;

import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.*;
import org.apache.camel.model.rest.RestDefinition;

public class InConnector {
  @Getter private final InConnectorDefinition connector;
  @Getter private RouteBuilder routeBuilder;

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
    routeBuilder = anonymousDummyRouteBuilder();
    connector.setRouteBuilder(routeBuilder);
    connector.setDefinition();
  }


  //TODO Merge
  private RestInEndpoint restInEndpoint;
  // TODO: Use different mechanism to detect this
  @Getter @Setter private Boolean registeredInCamel = false;

  @Setter RouteConfigurationBuilder configuration;

  public abstract void configure();

  public void configureOnException() {}

  public void handleResponse(RouteDefinition route) {}

  protected RouteDefinition from(InEndpoint inEndpoint) {
    routeBuilder = getRouteBuilderInstance();
    return routeBuilder.from(getInEndpointUri(inEndpoint.getId()));
  }

  protected RouteDefinition from(RestDefinition restDefinition) {
    restDefinition.to("direct:rest-" + restInEndpoint.getUri());
    return routeBuilder.from("direct:rest-" + restInEndpoint.getUri());
  }

  protected RestDefinition rest(String uri, String id) {
    routeBuilder = getRouteBuilderInstance();
    restInEndpoint = RestInEndpoint.instance(uri, id, routeBuilder);
    return restInEndpoint.rest();
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    routeBuilder = getRouteBuilderInstance();
    OnExceptionDefinition last = null;

    for (Class<? extends Throwable> ex : exceptions) {
      last = (last == null ? this.routeBuilder.onException(ex) : last.onException(ex));
    }
    return last;
  }

  public void createNewRouteBuilder() {
    routeBuilder = anonymousDummyRouteBuilder(configuration);
  }

  private RouteBuilder getRouteBuilderInstance() {
    if (routeBuilder == null) {
      return anonymousDummyRouteBuilder(configuration);
    }
    return routeBuilder;
  }
}
