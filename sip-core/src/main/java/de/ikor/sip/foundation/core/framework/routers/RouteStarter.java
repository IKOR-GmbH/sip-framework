package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.definitions.TestRouteBinder;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouterDefinition> availableRouters;

  public RouteStarter(RoutersAnnotationBasedFactory routersFactory) {
    this.availableRouters = routersFactory.getRouters();
  }

  @Override
  public void notify(CamelEvent event) {
    StaticRouteBuilderHelper.setCamelContext(
        ((CamelEvent.CamelContextInitializingEvent) event).getContext());
    availableRouters
        .forEach(this::configureDefinition);
    availableRouters.stream().map(CentralRouterDefinition::toCentralRouter)
            .forEach(this::buildRoutes);
  }

  void buildRoutes(CentralRouter router) {
    buildActiveRoutes(router);
    buildTestRoutes(router);
  }

  public void configureDefinition(CentralRouterDefinition router) {
    try {
      router.defineTopology();//Is This the key point?
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void buildActiveRoutes(CentralRouter router) {
    RouteBinder actualRouteBinder =
        new RouteBinder(router.getScenario(), router.getCentralModelRequestClass());

      router.configureOnException();
      router.defineTopology();
      router
          .getOutTopologyDefinition()
          .forEach(
              (outConnectors, s) -> {
                bindOutConnectors(actualRouteBinder, outConnectors, s);
              });


  }

  void buildTestRoutes(CentralRouter router) {
    TestRouteBinder testingRouteBinder =
            new TestRouteBinder(router.getScenario(), router.getCentralModelRequestClass());

      CentralEndpointsRegister.putInTestingState();
      router.configureOnException();
      router.defineTopology();
      CentralEndpointsRegister.putInActualState();

      router
              .getOutTopologyDefinition()
              .forEach(
                      (outConnectors, s) -> {
                        bindOutConnectors(testingRouteBinder, outConnectors, s);
                      });

  }

  private void bindOutConnectors(RouteBinder routeBinder, OutConnector[] outConnectors, String s) {
    if ("seq".equals(s)) {
      routeBinder.appendOutConnectorsSeq(outConnectors);
    } else if ("par".equals(s)) {
      routeBinder.appendOutConnectorsParallel(outConnectors);
    }
  }

  void populateTestingRoute(InConnector connector, CentralRouter router) throws Exception {
    connector.setDefinition();
    connector.configure();
    router.appendSipMCAndRouteId(
        connector.getConnectorTestingRouteDefinition(),
        connector.getName(),
        TestingRoutesUtil.TESTING_SUFFIX);
    connector
        .getConnectorTestingRouteDefinition()
        .getOutputs()
        .forEach(TestingRoutesUtil::handleTestIDAppending);
    connector.handleResponse(connector.getConnectorTestingRouteDefinition());
    // TODO Sta ako u handle response ima neki outConnector? Nece biti pokriven
    // handleTestIdAppending methodom
    camelContext().addRoutes(connector.getRouteBuilder());
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
