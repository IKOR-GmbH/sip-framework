package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RouteDefinition;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition centralRouterDefinition;
  private List<InConnector> inConnectors;

  public void setUpRoutes() {
    configureDefinition(centralRouterDefinition);
    buildActiveRoutes();
  }

  public void buildTopology() {
    inConnectors =
        centralRouterDefinition.getInConnectorDefinitions().stream()
            .map(InConnectorDefinition::toInConnector)
            .collect(Collectors.toList());
    inConnectors.forEach(this::configure);
    inConnectors.forEach(this::addToContext);
  }

  private void configure(InConnector inConnector) {
    inConnector.setDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(inConnector.getConnectorRouteDefinition(), inConnector.getName());
//    appendCDMValidatorIfResponseIsExpected(inConnector.getConnectorRouteDefinition());
    //TODO fix validator first

    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  void appendSipMCAndRouteId(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .to("sipmc:" + centralRouterDefinition.getScenario())
        // TODO double id set
        .routeId(generateRouteId(centralRouterDefinition.getScenario(), connectorName));
  }

  public String getScenario() {
    return centralRouterDefinition.getScenario();
  }

  public Class<?> getCentralModelRequestClass() {
    return centralRouterDefinition.getCentralModelRequestClass();
  }

  public void buildOnException() {
    centralRouterDefinition.configureOnException();
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public UseCaseTopologyDefinition getOutTopologyDefinition() {
    return centralRouterDefinition.getDefinition();
  }

  private void appendCDMValidatorIfResponseIsExpected(RouteDefinition connectorRouteDefinition) {
    Class<?> centralModelResponseClass = centralRouterDefinition.getCentralModelResponseClass();
    connectorRouteDefinition.process(new CDMValidator(centralModelResponseClass));
  }

  public void configureDefinition(CentralRouterDefinition router) {
    try {
      router.defineTopology();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  void buildActiveRoutes() {
    RouteBinder actualRouteBinder =
            new RouteBinder(this.getScenario(), this.getCentralModelRequestClass());

    this.buildOnException();
    this.buildTopology();
    UseCaseTopologyDefinition outTopologyDefinition = this.getOutTopologyDefinition();
    bindOutConnectors(actualRouteBinder, outTopologyDefinition);
  }

  private void bindOutConnectors(
          RouteBinder routeBinder, UseCaseTopologyDefinition outTopologyDefinition) {
    routeBinder.appendOutConnectorsSeq(outTopologyDefinition.getConnectorsBindInSequence());
    routeBinder.appendOutConnectorsParallel(outTopologyDefinition.getConnectorsBindInParallel());
  }
}
