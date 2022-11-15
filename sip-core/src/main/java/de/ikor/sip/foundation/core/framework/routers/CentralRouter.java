package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition centralRouterDefinition;
  private List<InConnector> inConnectors;

  public void buildTopology() {
    inConnectors =
        centralRouterDefinition.getInConnectorDefinitions().stream()
            .map(InConnectorDefinition::toInConnector)
            .collect(Collectors.toList());
    inConnectors.forEach(this::configure);
    inConnectors.forEach(this::addToContext);
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

  public void buildOnException() {
    centralRouterDefinition.configureOnException();
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO Handle exception
    }
  }

  private void configure(InConnector inConnector) {
    inConnector.setDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(
        inConnector.getConnectorRouteDefinition(),
        inConnector.getName(),
        CentralEndpointsRegister.suffixForCurrentState());
    appendCDMValidatorIfResponseIsExpected(inConnector.getConnectorRouteDefinition());

    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  public Map<OutConnector[], String> getOutTopologyDefinition() {
    return centralRouterDefinition.getDefinition().getAllConnectors();
  }

  private void appendCDMValidatorIfResponseIsExpected(RouteDefinition connectorRouteDefinition) {
    Class<?> centralModelResponseClass = centralRouterDefinition.getCentralModelResponseClass();
    connectorRouteDefinition.process(new CDMValidator(centralModelResponseClass));
  }
}
