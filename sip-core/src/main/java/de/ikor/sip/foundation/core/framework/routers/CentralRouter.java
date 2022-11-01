package de.ikor.sip.foundation.core.framework.routers;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;

public abstract class CentralRouter {

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
      connector.handleResponse(connector.getConnectorRouteDefinition());
    }
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition(this.getScenario());
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
    appendToSIPmcAndRouteId(connector, TestingRoutesUtil.TESTING_SUFFIX);
    connector
        .getConnectorDefinition()
        .getOutputs()
        .forEach(TestingRoutesUtil::handleTestIDAppending);
    connector.handleResponse(connector.getConnectorRouteDefinition());
  }

  List<InConnector> getInConnectors() {
    return inConnectors;
  }

  UseCaseTopologyDefinition getUseCaseDefinition() {
    return definition;
  }

  private void appendToSIPmcAndRouteId(InConnector connector, String routeSuffix) {
    connector
        .getConnectorRouteDefinition()
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
