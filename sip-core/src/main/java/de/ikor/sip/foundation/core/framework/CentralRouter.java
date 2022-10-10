package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import static java.lang.String.format;

public abstract class CentralRouter {
  @Getter @Setter private static CamelContext camelContext;

  public abstract String getScenario();

  private UseCaseTopologyDefinition definition;

  public abstract void configure() throws Exception;

  public UseCaseTopologyDefinition from(InConnector... inConnectors) throws Exception {
    for (InConnector connector : inConnectors) {
      connector.configure();
      appendToSIPmcAndRouteId(connector);
      connector.handleResponse(connector.getConnectorDefinition());
      camelContext.addRoutes(connector.getRouteBuilder());
      generateTestingConnectorRoute(connector);
    }
    definition = new UseCaseTopologyDefinition(camelContext, this.getScenario());
    return definition;
  }

  private void generateTestingConnectorRoute(InConnector connector) throws Exception {
    CentralOutEndpointsRegister.setState("testing");
    connector.configure();
    appendToSIPmcAndRouteId(connector, "-testing");
    connector.handleResponse(connector.getConnectorDefinition());
    camelContext.addRoutes(connector.getRouteBuilder());
    CentralOutEndpointsRegister.setState("actual");
  }

  private void appendToSIPmcAndRouteId(InConnector connector, String routeSuffix) {
    connector
        .getConnectorDefinition()
        .to("sipmc:" + this.getScenario() + routeSuffix)
        .routeId(format("%s-%s%s", this.getScenario(), connector.getName(), routeSuffix));
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
