package de.ikor.sip.foundation.core.framework;

import static java.lang.String.format;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public abstract class CentralRouter {
  @Getter @Setter private static CamelContext camelContext;

  public abstract String getScenario();

  private UseCaseTopologyDefinition definition;

  public abstract void configure() throws Exception;

  public abstract void configureOnCentralRouterLevel();

  public UseCaseTopologyDefinition from(InConnector... inConnectors) throws Exception {
    for (InConnector connector : inConnectors) {
      connector.configureOnConnectorLevel();
      connector.configure();
      appendToSIPmcAndRouteId(connector);
      connector.handleResponse(connector.getConnectorDefinition());
      if (connector.getRestBuilder() != null) {
        camelContext.addRoutes(connector.getRestBuilder());
      }
      camelContext.addRoutes(connector.getRouteBuilder());
      //      camelContext.addRoutesConfigurations();

      CentralEndpointsRegister.setState("testing");
      generateTestingConnectorRoute(connector);
      CentralEndpointsRegister.setState("actual");
    }

    definition = new UseCaseTopologyDefinition(camelContext, this.getScenario());
    return definition;
  }

  public static String generateRouteId(
      String scenarioName, String connectorName, String routeSuffix) {
    return format("%s-%s%s", scenarioName, connectorName, routeSuffix);
  }

  void buildOutgoingConnector() throws Exception {
    if (definition != null) {
      this.definition.build();
    } else {
      throw new EmptyCentralRouterException(this.getScenario());
    }
  }

  private void generateTestingConnectorRoute(InConnector connector) throws Exception {
    connector.createNewRouteBuilder();
    connector.configureOnConnectorLevel();
    connector.configure();
    appendToSIPmcAndRouteId(connector, "-testing");
    connector.handleResponse(connector.getConnectorDefinition());
    if (connector.getRestBuilder() != null) {
      camelContext.addRoutes(connector.getRestBuilder());
    }
    camelContext.addRoutes(connector.getRouteBuilder());
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

  public static CamelContext getCamelContext() {
    return camelContext;
  }

  public static RouteBuilder anonymousDummyRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        // no need for implementation; used for building routes
      }
    };
  }
}
