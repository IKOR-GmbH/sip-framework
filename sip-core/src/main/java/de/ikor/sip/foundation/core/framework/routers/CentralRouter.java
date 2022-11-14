package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.official.CentralRouterDefinition;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.Map;

import static de.ikor.sip.foundation.core.framework.routers.RouteStarter.getCamelContext;
import static java.lang.String.format;

@RequiredArgsConstructor
public class CentralRouter {
  private final CentralRouterDefinition centralRouterDefinition;

  void appendSipMCAndRouteId(
      RouteDefinition routeDefinition, String connectorName, String routeSuffix) {
    routeDefinition
        .to("sipmc:" + centralRouterDefinition.getScenario() + routeSuffix)
        .routeId(
            generateRouteId(centralRouterDefinition.getScenario(), connectorName, routeSuffix));
  }

  public static String generateRouteId(
      String scenarioName, String connectorName, String routeSuffix) {
    return format("%s-%s%s", scenarioName, connectorName, routeSuffix);
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

  public void defineTopology() throws Exception {
    centralRouterDefinition.defineTopology();
    centralRouterDefinition.getInConnectors().forEach(this::configure);
//    centralRouterDefinition.getInConnectors().forEach(this::addToContext);
  }

  private void addToContext(InConnector inConnector) {
    try {
      getCamelContext().addRoutes(inConnector.getRouteBuilder());
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

  public static RouteBuilder anonymousDummyRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() {
        // no need for implementation; used for building routes
      }
    };
  }
}
