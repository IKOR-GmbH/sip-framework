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
    RouteDefinition multicastDefinition =
        new RouteDefinition()
            .from(format("sipmc:%s", useCase))
            .process(new CDMValidator(centralDomainRequest))
            .routeId(format("sipmc-bridge-%s", useCase));
    MulticastDefinition multicastDefinition1 =
        multicastDefinition.multicast().parallelProcessing(isParallel);
    Stream.of(outConnectors)
        .forEach(
            outConnector ->
                multicastDefinition1.to(DIRECT_URI_PREFIX + outConnector.getName()));
    multicastDefinition1.end();
    return multicastDefinition;
  }

  private FromMiddleComponentRouteTemplateBuilder(String useCase) {
    this.useCase = useCase;
  }
}
