package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil.TESTING_SUFFIX;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.*;

@RequiredArgsConstructor
public class UseCaseTopologyDefinition {
  private static final String URI_PREFIX = "direct:";
  private final String useCase;

  @Getter private final List<RouteBuilder> outConnectorsRouteBuilders = new ArrayList<>();
  private ProcessorDefinition routeDefinition = null;
  private ProcessorDefinition testRouteDefinition = null;
  @Getter private RouteBuilder routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
  @Getter private RouteBuilder testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder();

  public UseCaseTopologyDefinition to(OutConnector... outConnectors) {
    routeBuilder = CentralRouter.anonymousDummyRouteBuilder();
    routeDefinition = initBaseRoute(routeDefinition, "");
    if (outConnectors.length > 1) {
      routeDefinition = appendMulticastDefinition(outConnectors, routeDefinition, "");
    } else {
      OutConnector outConnector = outConnectors[0];
      routeDefinition.to(URI_PREFIX + outConnector.getName());
      this.from(outConnector, URI_PREFIX + outConnector.getName(), "");
    }
    routeBuilder.getRouteCollection().getRoutes().add((RouteDefinition) routeDefinition);
    generateTestRoutes(outConnectors);
    return this;
  }

  private void generateTestRoutes(OutConnector... outConnectors) {
    testingRouteBuilder = CentralRouter.anonymousDummyRouteBuilder();
    testRouteDefinition = initBaseRoute(testRouteDefinition, TESTING_SUFFIX);
    CentralEndpointsRegister.setState("testing");
    if (outConnectors.length > 1) {
      testRouteDefinition =
          appendMulticastDefinition(outConnectors, testRouteDefinition, TESTING_SUFFIX);
    } else {
      OutConnector outConnector = outConnectors[0];
      testRouteDefinition =
          testRouteDefinition.to(URI_PREFIX + outConnector.getName() + TESTING_SUFFIX);
      this.from(outConnector, URI_PREFIX + outConnector.getName(), TESTING_SUFFIX);
    }
    testingRouteBuilder.getRouteCollection().getRoutes().add((RouteDefinition) testRouteDefinition);
    CentralEndpointsRegister.setState("actual");
  }

  private ProcessorDefinition appendMulticastDefinition(
      OutConnector[] outConnectors, ProcessorDefinition processorDefinition, String suffix) {
    MulticastDefinition multicastDefinition = processorDefinition.multicast().parallelProcessing();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to(URI_PREFIX + outConnector.getName() + suffix);
              this.from(outConnector, URI_PREFIX + outConnector.getName(), suffix);
            });
    return multicastDefinition.end();
  }

  private ProcessorDefinition initBaseRoute(
      ProcessorDefinition processorDefinition, String suffix) {
    String uri = "sipmc:" + useCase + suffix;
    RouteDefinition rd = new RouteDefinition();

    return processorDefinition == null
        ? rd.from(uri).routeId("sipmc-bridge-" + useCase + suffix)
        : processorDefinition;
  }

  @SneakyThrows
  private UseCaseTopologyDefinition from(
      OutConnector outConnector, String uri, String routeSuffix) {
    RouteBuilder rb = CentralRouter.anonymousDummyRouteBuilder();
    outConnector.setRouteBuilder(rb);
    outConnector.configureOnException();
    String routeId = CentralRouter.generateRouteId(useCase, outConnector.getName(), routeSuffix);
    RouteDefinition routeDefinition = rb.from(uri + routeSuffix).routeId(routeId);
    outConnector.configure(routeDefinition);
    // add testing suffix to processor definition id to prevent id duplication
    if (TESTING_SUFFIX.equals(routeSuffix)) {
      routeDefinition.getOutputs().forEach(TestingRoutesUtil::handleTestIDAppending);
    }
    outConnectorsRouteBuilders.add(rb);

    return this;
  }
}
