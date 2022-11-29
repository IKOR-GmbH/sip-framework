package de.ikor.sip.foundation.core.framework;

import static java.lang.String.format;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;

public class StaticRouteBuilderHelper {
  private static CamelContext camelContext;

  private StaticRouteBuilderHelper() {}

  public static RouteBuilder anonymousDummyRouteBuilder(RouteConfigurationBuilder configuration) {
    RouteBuilder routeBuilder =
            new RouteBuilder() {
              @Override
              public void configure() {
                // no need for implementation; used for building routes
              }
            };
    appendConfig(routeBuilder, configuration);
    return routeBuilder;
  }

  public static RouteConfigurationBuilder anonymousDummyRouteConfigurationBuilder() {
    return new RouteConfigurationBuilder() {
      @Override
      public void configuration() {
          // dummy builder
      }
    };
  }

  public static String generateRouteId(String scenarioName, String connectorName) {
    return format("%s-%s", scenarioName, connectorName);
  }

  public static CamelContext camelContext() {
    return camelContext;
  }

  public static void setCamelContext(CamelContext context) {
    camelContext = context;
  }

  private static void appendConfig(
          RouteBuilder routeBuilder, RouteConfigurationBuilder configuration) {
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
