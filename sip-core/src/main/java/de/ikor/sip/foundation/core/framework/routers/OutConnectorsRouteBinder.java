package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplateBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousRouteBuilder;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

@RequiredArgsConstructor
public class OutConnectorsRouteBinder {
  @Getter protected final List<RouteBuilder> outConnectorsBuilders = new ArrayList<>();
  private final Scenario scenario;

  protected void appendOutConnectorsSeq(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, false);
  }

  public void appendOutConnectorsParallel(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, true);
  }

  private void appendConnectors(OutConnector[] outConnectors, boolean isParallel) {
    if (outConnectors == null) {
      return;//TODO handle on different place
    }
    RouteDefinition route =
        FromMiddleComponentRouteTemplateBuilder.withUseCase(scenario.getName())
            .outConnectors(outConnectors)
            .inParallel(isParallel)
            .createRoute();

    RouteBuilder builder = anonymousRouteBuilder(scenario.getScenarioRoutesConfiguration());
    builder.getRouteCollection().getRoutes().add(route);
    addToContext(builder);

    FromDirectOutConnectorRouteTemplate template =
        new FromDirectOutConnectorRouteTemplate(scenario.getName(), scenario.getScenarioRoutesConfiguration());
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
