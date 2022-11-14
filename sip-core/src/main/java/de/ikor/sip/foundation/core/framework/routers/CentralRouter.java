package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.Map;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition centralRouterDefinition;

  public void defineTopology() throws Exception {
    centralRouterDefinition.defineTopology();
    centralRouterDefinition.getInConnectors().forEach(this::configure);
//    centralRouterDefinition.getInConnectors().forEach(this::addToContext);
  }

  void appendSipMCAndRouteId(
      RouteDefinition routeDefinition, String connectorName, String routeSuffix) {
    routeDefinition
        .to("sipmc:" + centralRouterDefinition.getScenario() + routeSuffix)
        .routeId(
            generateRouteId(centralRouterDefinition.getScenario(), connectorName, routeSuffix));
  }

  public String getScenario() {
    return centralRouterDefinition.getScenario();
  }

  public Class<?> getCentralModelRequestClass() {
    return centralRouterDefinition.getCentralModelRequestClass();
  }

  public void configureOnException() {
    centralRouterDefinition.configureOnException();
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);//TODO Handle exception
    }
  }

  private void configure(InConnector inConnector) {
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(inConnector.getConnectorRouteDefinition(), inConnector.getName(), "");
    appendCDMValidatorIfResponseIsExpected(inConnector.getConnectorRouteDefinition());

    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  public List<InConnector> getInConnectors() {
    return centralRouterDefinition.getInConnectors();
  }

  public Map<OutConnector[], String> getOutTopologyDefinition() {
    return centralRouterDefinition.getDefinition().getAllConnectors();
  }

  private void appendCDMValidatorIfResponseIsExpected(RouteDefinition connectorRouteDefinition) {
    Class<?> centralModelResponseClass = centralRouterDefinition.getCentralModelResponseClass();
    connectorRouteDefinition.process(new CDMValidator(centralModelResponseClass));
  }
}
