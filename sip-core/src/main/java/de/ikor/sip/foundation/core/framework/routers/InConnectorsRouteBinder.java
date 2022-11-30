package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorService;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;

public class InConnectorsRouteBinder {
  private final Scenario scenario;

  public InConnectorsRouteBinder(Scenario scenario) {
    this.scenario = scenario;
  }

  public void bindInConnectors(List<InConnectorService> inConnectorServices) {
    inConnectorServices.forEach(this::configure);
    inConnectorServices.forEach(this::addToContext);
  }

  private void configure(InConnectorService inConnectorService) {
    inConnectorService.setConfiguration(scenario.getScenarioRoutesConfiguration());
    inConnectorService.initDefinition();
    inConnectorService.configureOnException();
    inConnectorService.configure();
    appendMiddleRouting(inConnectorService.getConnectorRouteDefinition(), inConnectorService.getName());
    inConnectorService.handleResponse(inConnectorService.getConnectorRouteDefinition());
  }

  private void appendMiddleRouting(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .process(new CDMValidator(scenario.getCdmRequestType()))
        .to("sipmc:" + scenario.getName())
        .process(new CDMValidator(scenario.getCdmResponseType()))
        // TODO double id set; @Nemanja -> can you specify the case
        .routeId(generateRouteId(scenario.getName(), connectorName));
  }

  private void addToContext(InConnectorService inConnectorService) {
    try {
      camelContext().addRoutes(inConnectorService.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
