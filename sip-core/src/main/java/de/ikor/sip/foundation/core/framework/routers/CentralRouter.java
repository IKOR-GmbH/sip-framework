package de.ikor.sip.foundation.core.framework.routers;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public abstract class CentralRouter {
  @Getter @Setter private static CamelContext camelContext;

  private final List<InConnector> inConnectors = new ArrayList<>();

  private UseCaseTopologyDefinition definition;

  public abstract String getScenario();

  public abstract void configure() throws Exception;

  public void configureOnException() {}

  public UseCaseTopologyDefinition from(InConnector... inConnectors) {
    for (InConnector connector : inConnectors) {
      connector.configureOnException();
      connector.configure();
      appendToSIPmcAndRouteId(connector);
      connector.handleResponse(connector.getConnectorDefinition());
    }
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition(camelContext, this.getScenario());
    return definition;
  }

  public static String generateRouteId(
      String scenarioName, String connectorName, String routeSuffix) {
    return format("%s-%s%s", scenarioName, connectorName, routeSuffix);
  }

  void switchToTestingDefinitionMode(InConnector connector) {
    connector.createNewRouteBuilder();
    connector.configureOnException();
    connector.configure();
    appendToSIPmcAndRouteId(connector, "-testing");
    connector.handleResponse(connector.getConnectorDefinition());
  }

  List<InConnector> getInConnectors() {
    return inConnectors;
  }

  UseCaseTopologyDefinition getUseCaseDefinition() {
    return definition;
  }

  private void appendToSIPmcAndRouteId(InConnector connector, String routeSuffix) {
    connector
        .getConnectorDefinition()
        .to("sipmc:" + this.getScenario() + routeSuffix)
        .routeId(generateRouteId(this.getScenario(), connector.getName(), routeSuffix));
  }

  private void appendToSIPmcAndRouteId(InConnector connector) {
    appendToSIPmcAndRouteId(connector, "");
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
