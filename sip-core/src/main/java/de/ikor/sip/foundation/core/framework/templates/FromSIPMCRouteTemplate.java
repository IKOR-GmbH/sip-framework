package de.ikor.sip.foundation.core.framework.templates;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import java.util.stream.Stream;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;

public class FromSIPMCRouteTemplate {
  private final String useCase;
  private boolean isParallel;
  private OutConnector[] outConnectors;
  protected static final String DIRECT_URI_PREFIX = "direct:";

  public static FromSIPMCRouteTemplate withUseCase(String useCase) {
    return new FromSIPMCRouteTemplate(useCase);
  }

  public FromSIPMCRouteTemplate outConnectors(OutConnector[] outConnectors) {
    this.outConnectors = outConnectors;
    return this;
  }

  public FromSIPMCRouteTemplate inParallel(boolean b) {
    this.isParallel = b;
    return this;
  }

  public RouteDefinition createRoute() {
    RouteDefinition routeDefinition = createNewFromSipMcDefinition();
    appendMulticastToOutConnectors(routeDefinition);
    return routeDefinition;
  }

  private RouteDefinition createNewFromSipMcDefinition() {
    return new RouteDefinition()
        .from(appendUseCase("sipmc:%s"))
        .routeId(appendUseCase("sipmc-bridge-%s"));
  }

  private String appendUseCase(String format) {
    return format(format, useCase);
  }

  private void appendMulticastToOutConnectors(RouteDefinition routeDefinition) {
    MulticastDefinition multicastDefinition =
        routeDefinition.multicast().parallelProcessing(isParallel);
    Stream.of(outConnectors)
        .forEach(
            outConnector -> multicastDefinition.to(DIRECT_URI_PREFIX + outConnector.getName()));
    multicastDefinition.end();
  }

  private FromSIPMCRouteTemplate(String useCase) {
    this.useCase = useCase;
  }
}