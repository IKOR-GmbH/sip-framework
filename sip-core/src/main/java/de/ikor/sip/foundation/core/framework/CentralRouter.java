package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import static java.lang.String.format;

public abstract class CentralRouter {
  @Getter @Setter private static CamelContext camelContext;

  public abstract String getUseCase();
  private UseCaseTopologyDefinition definition;

  public abstract void configure() throws Exception;

  public UseCaseTopologyDefinition from(InConnector... inConnectors) throws Exception {
    for (InConnector connector : inConnectors) {
      connector.configure();
      connector
          .getConnectorDefinition()
          .to("sipmc:" + this.getUseCase())
          .routeId(format("%s-%s", this.getUseCase(), connector.getName()));
      camelContext.addRoutes(connector.getRouteBuilder());
    }
    definition = new UseCaseTopologyDefinition(camelContext, this.getUseCase());
    return definition;
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
