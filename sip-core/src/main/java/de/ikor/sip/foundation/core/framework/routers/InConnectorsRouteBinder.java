package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.ConnectorStarter;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;

public class InConnectorsRouteBinder {
  private final Scenario scenario;

  public InConnectorsRouteBinder(Scenario scenario) {
    this.scenario = scenario;
  }

  public void bindInConnectors(List<InConnector> inConnectorServices) {
    inConnectorServices.forEach(this::configure);
    inConnectorServices.forEach(this::addToContext);
  }

  private void configure(InConnector inConnector) {
    ConnectorStarter.initConnector(inConnector, scenario.getScenarioRoutesConfiguration());
    inConnector.configureOnException();
    inConnector.configure();
    appendMiddleRouting(getFirstDef(inConnector.getRouteBuilder()), inConnector.getName());
    inConnector.handleResponse(getFirstDef(inConnector.getRouteBuilder()));
  }

  private void appendMiddleRouting(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .process(new CDMValidator(scenario.getCdmRequestType()))
        .to("sipmc:" + scenario.getName())
        .process(new CDMValidator(scenario.getCdmResponseType()))
        // TODO double id set; @Nemanja -> can you specify the case
        .routeId(generateRouteId(scenario.getName(), connectorName));
  }

  private void addToContext(InConnector inConnectorService) {
    try {
      camelContext().addRoutes(inConnectorService.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private RouteDefinition getFirstDef(RouteBuilder routeBuilder) {
    // temporary method
    // TODO is it OK to loop trough all routes? (testing routes should have different builder)
    return routeBuilder.getRouteCollection().getRoutes().get(0);
  }
}
