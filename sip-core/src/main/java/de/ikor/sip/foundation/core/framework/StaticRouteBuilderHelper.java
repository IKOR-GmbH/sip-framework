package de.ikor.sip.foundation.core.framework;

import static java.lang.String.format;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public class StaticRouteBuilderHelper {
  private static CamelContext camelContext;

  private StaticRouteBuilderHelper() {}

  public static RouteBuilder anonymousDummyRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() {
        // no need for implementation; used for building routes
      }
    };
  }

  public static String generateRouteId(
      String scenarioName, String connectorName) {
    return format("%s-%s", scenarioName, connectorName);
  }

  public static CamelContext camelContext() {
    return camelContext;
  }

  public static void setCamelContext(CamelContext context) {
    camelContext = context;
  }
}
