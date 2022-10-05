package de.ikor.sip.foundation.core.framework;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MulticastDefinition;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
  private final CamelContext camelContext;
  private final String useCase;

  private MulticastDefinition multicastDefinition = null;
  @Getter private final RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();

  public UseCaseTopologyDefinition to(OutConnector... outConnectors) throws Exception {
    multicastDefinition =
        initMulticastRoute(routeBuilder);
    multicastDefinition = appendParallelProcessingIfMultipleConnectors(outConnectors);

    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to("direct:" + outConnector.getName());
              this.from(outConnector, "direct:" + outConnector.getName());
            });
    return this;
  }

  private MulticastDefinition appendParallelProcessingIfMultipleConnectors(OutConnector[] outConnectors) {
    if(outConnectors.length > 1) {
      multicastDefinition = multicastDefinition.parallelProcessing();
    }
    return multicastDefinition;
  }

  public void build() throws Exception {
    camelContext.addRoutes(this.routeBuilder);
  }

  private MulticastDefinition initMulticastRoute(RouteBuilder routeBuilder) {
    return multicastDefinition == null
        ? multicastDefinition = routeBuilder.from("sipmc:" + useCase).multicast()
        : multicastDefinition;
  }

  @SneakyThrows
  private UseCaseTopologyDefinition from(OutConnector outConnector, String uri) {
    RouteBuilder rb = CentralRouter.anonymousDummyRouteBuilder();
    outConnector.configure(rb.from(uri));
    camelContext.addRoutes(rb);

    return this;
  }
}
