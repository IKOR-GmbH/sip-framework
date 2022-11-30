package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;

public class InConnectorsRouteBinder {
  private final Scenario scenario;

  public InConnectorsRouteBinder(Scenario scenario) {
    this.scenario = scenario;
  }

  public void bindInConnectors(List<InConnector> inConnectors) {
    inConnectors.forEach(this::configure);
    inConnectors.forEach(this::addToContext);
  }

  private void configure(InConnector inConnector) {
    inConnector.setConfiguration(scenario.getScenarioRoutesConfiguration());
    inConnector.initDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendMiddleRouting(inConnector.getConnectorRouteDefinition(), inConnector.getName());
    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  private void appendMiddleRouting(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .process(new CDMValidator(scenario.getCdmRequestType()))
        .to("sipmc:" + scenario.getName())
        .process(new CDMValidator(scenario.getCdmResponseType()))
        // TODO double id set; @Nemanja -> can you specify the case
        .routeId(generateRouteId(scenario.getName(), connectorName));
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
