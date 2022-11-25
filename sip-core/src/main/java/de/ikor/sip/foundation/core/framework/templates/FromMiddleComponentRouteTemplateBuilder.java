package de.ikor.sip.foundation.core.framework.templates;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.routers.CDMValidator;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;

import java.util.stream.Stream;

import static java.lang.String.format;

public class FromMiddleComponentRouteTemplateBuilder {
  private final String useCase;
  private boolean isParallel;
  private Class<?> centralDomainRequest;
  private OutConnectorDefinition[] outConnectors;
  protected static final String DIRECT_URI_PREFIX = "direct:";

  public static FromMiddleComponentRouteTemplateBuilder withUseCase(String useCase) {
    return new FromMiddleComponentRouteTemplateBuilder(useCase);
  }

  public FromMiddleComponentRouteTemplateBuilder withCentralDomainRequest(Class<?> requestType) {
    this.centralDomainRequest = requestType;
    return this;
  }

  public FromMiddleComponentRouteTemplateBuilder outConnectors(
      OutConnectorDefinition[] outConnectors) {
    this.outConnectors = outConnectors;
    return this;
  }

  public FromMiddleComponentRouteTemplateBuilder inParallel(boolean b) {
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

  private FromMiddleComponentRouteTemplateBuilder(String useCase) {
    this.useCase = useCase;
  }
}
