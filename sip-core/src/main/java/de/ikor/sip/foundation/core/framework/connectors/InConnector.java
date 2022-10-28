package de.ikor.sip.foundation.core.framework.connectors;

import static de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister.getInEndpointUri;
import static de.ikor.sip.foundation.core.framework.routers.CentralRouter.anonymousDummyRouteBuilder;

import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.RestInEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.*;
import org.apache.camel.model.rest.RestDefinition;

public abstract class InConnector implements Connector {
  @Getter private RouteBuilder routeBuilder;
  @Getter private RouteBuilder restBuilder;
  private RestInEndpoint restInEndpoint;
  // TODO: Use different mechanism to detect this
  @Getter @Setter private Boolean registeredInCamel = false;

  @Setter RouteConfigurationBuilder configuration;

  public abstract void configure();

  public void configureOnException() {}

  public void handleResponse(RouteDefinition route) {}

  protected RouteDefinition from(InEndpoint inEndpoint) {
    routeBuilder = getRouteBuilderInstance();
    appendConfig();
    return routeBuilder.from(getInEndpointUri(inEndpoint.getId()));
  }

  protected RouteDefinition from(RestDefinition restDefinition) {
    restDefinition.to("direct:rest-" + restInEndpoint.getUri());
    routeBuilder = anonymousDummyRouteBuilder();
    return routeBuilder.from("direct:rest-" + restInEndpoint.getUri());
  }

  protected RestDefinition rest(String uri, String id) {
    restBuilder = getRouteBuilderInstance();
    restInEndpoint = RestInEndpoint.instance(uri, id, restBuilder);
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

  private void appendConfig() {
    configuration
        .getRouteConfigurationCollection()
        .getRouteConfigurations()
        .forEach(
            routeConfigurationDefinition -> {
              routeConfigurationDefinition
                  .getIntercepts()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getIntercepts()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getInterceptFroms()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getInterceptFroms()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getOnCompletions()
                  .forEach(
                      onCompletionDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getOnCompletions()
                              .add(onCompletionDefinition));
              routeConfigurationDefinition
                  .getInterceptSendTos()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getInterceptSendTos()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getOnExceptions()
                  .forEach(
                      onExceptionDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getOnExceptions()
                              .add(onExceptionDefinition));
            });
  }
}
