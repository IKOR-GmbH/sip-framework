package de.ikor.sip.foundation.core.framework;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MulticastDefinition;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
  private static final String TESTING_SUFFIX = "-testing";
  private final CamelContext camelContext;
  private final String useCase;

  private MulticastDefinition multicastDefinition = null;
  private MulticastDefinition testingDefinition = null;
  @Getter private final RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
  private final RouteBuilder testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder();

  public UseCaseTopologyDefinition to(OutConnector... outConnectors) throws Exception {
    multicastDefinition = initMulticastRoute(routeBuilder, multicastDefinition, false);
    multicastDefinition =
        appendParallelProcessingIfMultipleConnectors(multicastDefinition, outConnectors);
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to("direct:" + outConnector.getName());
              this.from(outConnector, "direct:" + outConnector.getName(), "");
            });
    generateTestRoutes(outConnectors);
    return this;
  }

  private void generateTestRoutes(OutConnector... outConnectors) {
    testingDefinition = initMulticastRoute(testingRouteBuilder, testingDefinition, true);
    testingDefinition =
        appendParallelProcessingIfMultipleConnectors(testingDefinition, outConnectors);
    CentralEndpointsRegister.setState("testing");
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              testingDefinition.to("direct:" + outConnector.getName() + TESTING_SUFFIX);
              this.from(outConnector, "direct:" + outConnector.getName(), TESTING_SUFFIX);
            });
    CentralEndpointsRegister.setState("actual");
  }

  private MulticastDefinition appendParallelProcessingIfMultipleConnectors(
      MulticastDefinition multicastDefinition, OutConnector[] outConnectors) {
    if (outConnectors.length > 1) {
      return multicastDefinition.parallelProcessing();
    }
    return multicastDefinition;
  }

  public void build() throws Exception {
    camelContext.addRoutes(this.routeBuilder);
    camelContext.addRoutes(this.testingRouteBuilder);
  }

  private MulticastDefinition initMulticastRoute(
      RouteBuilder routeBuilder, MulticastDefinition multicastDefinition, boolean isTest) {
    String uri = "sipmc:" + useCase;
    if (isTest) {
      uri = uri.concat(TESTING_SUFFIX);
    }
    return multicastDefinition == null ? routeBuilder.from(uri).multicast() : multicastDefinition;
  }

  @SneakyThrows
  private UseCaseTopologyDefinition from(
      OutConnector outConnector, String uri, String routeSuffix) {
    RouteBuilder rb = CentralRouter.anonymousDummyRouteBuilder();
    String routeId = CentralRouter.generateRouteId(useCase, outConnector.getName(), routeSuffix);
    outConnector.configure(rb.from(uri + routeSuffix).routeId(routeId));
    camelContext.addRoutes(rb);

    return this;
  }
}
