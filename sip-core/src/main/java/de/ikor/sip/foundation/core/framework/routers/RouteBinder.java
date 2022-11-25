package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplateBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteBuilder;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

@RequiredArgsConstructor
public class RouteBinder {
  private final String useCase;
  private final Class<?> centralModelRequest;
  @Getter protected final List<RouteBuilder> outConnectorsBuilders = new ArrayList<>();

  protected void appendOutConnectorsSeq(OutConnectorDefinition[] outConnectors) {
    appendConnectors(outConnectors, false);
  }

  public void appendOutConnectorsParallel(OutConnectorDefinition[] outConnectors) {
    appendConnectors(outConnectors, true);
  }

  private void appendConnectors(OutConnectorDefinition[] outConnectors, boolean isParallel) {
    RouteDefinition route =
        FromMiddleComponentRouteTemplateBuilder.withUseCase(useCase)
            .withCentralDomainRequest(centralModelRequest)
            .outConnectors(outConnectors)
            .inParallel(isParallel)
            .createRoute();

    RouteBuilder builder = anonymousDummyRouteBuilder();
    builder.getRouteCollection().getRoutes().add(route);
    addToContext(builder);

    FromDirectOutConnectorRouteTemplate template = new FromDirectOutConnectorRouteTemplate(useCase);
    Arrays.stream(outConnectors).map(template::bindWithRouteBuilder).forEach(this::addToContext);
  }

  private void addToContext(RouteBuilder builder) {
    try {
      camelContext().addRoutes(builder);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
