package de.ikor.sip.foundation.core.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import static java.lang.String.format;

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
            String scenarioName, String connectorName, String routeSuffix) {
        return format("%s-%s%s", scenarioName, connectorName, routeSuffix);
    }

    public static CamelContext camelContext() {
        return camelContext;
    }

    public static void setCamelContext(CamelContext context) {
      camelContext = context;
    }
}
