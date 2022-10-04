package de.ikor.sip.foundation.core.framework;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
  private final CamelContext camelContext;
  private final String useCase;

  public UseCaseTopologyDefinition to(OutConnector outConnector)
      throws Exception { // todo lose exception
    RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
    outConnector.configure(routeBuilder.from("sipmc:" + useCase));
    camelContext.addRoutes(routeBuilder);

    return this;
  }

  public MulticastDefinition to(OutConnector... outConnectors) throws Exception {
    RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
    org.apache.camel.model.MulticastDefinition multicastDefinition =
        routeBuilder.from("sipmc:" + useCase).multicast().parallelProcessing();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to("direct:" + outConnector.getName());
              this.to(outConnector, "direct:" + outConnector.getName());
            });
    multicastDefinition.end();
    camelContext.addRoutes(routeBuilder);

    return new MulticastDefinition(useCase, multicastDefinition, camelContext);
  }

  @SneakyThrows
  private UseCaseTopologyDefinition to(OutConnector outConnector, String uri) {
    RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
    outConnector.configure(routeBuilder.from(uri));
    camelContext.addRoutes(routeBuilder);

    return this;
  }
}
