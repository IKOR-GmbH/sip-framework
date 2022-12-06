package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.configuredRouteBuilder;

import de.ikor.sip.foundation.core.framework.connectors.ConnectorStarter;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromSIPMCRouteTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

@RequiredArgsConstructor
public class OutConnectorsRouteBinder {
  @Getter protected final List<RouteBuilder> outConnectorsBuilders = new ArrayList<>();
  private final Scenario scenario;

  protected void appendOutConnectors(UseCaseTopologyDefinition definition) {
    definition.getConnectorsBindInParallel().ifPresent(this::appendParallel);
    definition.getConnectorsBindInSequence().ifPresent(this::appendSequenced);
  }

  private void appendParallel(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, true);
  }

  private void appendSequenced(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, false);
  }

  private void appendConnectors(OutConnector[] outConnectors, boolean isParallel) {
    initConnectors(outConnectors);
    createAndAddFromSIPMCRoute(outConnectors, isParallel);
    createAndAddFromDirectRoutes(outConnectors);
  }

  private void initConnectors(OutConnector[] outConnectors) {
    for (OutConnector connector : outConnectors) {
      ConnectorStarter.initConnector(connector, scenario.getScenarioRoutesConfiguration());
    }
  }

  private void createAndAddFromSIPMCRoute(OutConnector[] outConnectors, boolean isParallel) {
    RouteDefinition route =
        FromSIPMCRouteTemplate.withUseCase(scenario.getName())
            .outConnectors(outConnectors)
            .inParallel(isParallel)
            .createRoute();

    RouteBuilder builder = configuredRouteBuilder(scenario.getScenarioRoutesConfiguration());
    builder.getRouteCollection().getRoutes().add(route);
    addToContext(builder);
  }

  private void createAndAddFromDirectRoutes(OutConnector[] outConnectors) {
    FromDirectOutRouteTemplate template = new FromDirectOutRouteTemplate(scenario.getName());
    Arrays.stream(outConnectors).map(template::bindWithRouteBuilder).forEach(this::addToContext);
  }

  @SneakyThrows()
  private void addToContext(RouteBuilder builder) {
    camelContext().addRoutes(builder);
  }
}
