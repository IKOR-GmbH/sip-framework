package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.definitions.ActualRouteBinder;
import de.ikor.sip.foundation.core.framework.definitions.TestRouteBinder;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import java.util.List;

import de.ikor.sip.foundation.core.framework.official.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouterDefinition> availableRouters;
  public static CamelContext camelContext;

  public RouteStarter(RoutersAnnotationBasedFactory routersFactory) {
    this.availableRouters = routersFactory.getRouters();
  }

  @Override
  public void notify(CamelEvent event) {
    camelContext = ((CamelEvent.CamelContextInitializingEvent) event).getContext();
    CentralEndpointsRegister.setCamelContext(camelContext);
    availableRouters.stream().map(CentralRouterDefinition::toCentralRouter).forEach(this::buildRoutes);
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }

  void buildRoutes(CentralRouter router) {
    ActualRouteBinder actualRouteBinder =
        new ActualRouteBinder(router.getScenario(), router.getCentralModelRequestClass());
    TestRouteBinder testingRouteBinder =
            new TestRouteBinder(router.getScenario(), router.getCentralModelRequestClass());

    try {
      router.configureOnException();
      router.defineTopology();

      List<InConnector> inConnectors = router.getInConnectors();
      CentralEndpointsRegister.putInTestingState();
      inConnectors.forEach(inConnector -> populateTestingRoute(inConnector, router));
      CentralEndpointsRegister.putInActualState();

      for (InConnector connector : inConnectors) {
        camelContext.addRoutes(connector.getRouteBuilder());
      }

      router
          .getOutTopologyDefinition()
          .forEach((outConnectors, s) ->
          {bindOutConnectors(actualRouteBinder, outConnectors, s);
           bindOutConnectors(testingRouteBinder, outConnectors, s);});

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void bindOutConnectors(
      RouteBinder routeBinder, OutConnector[] outConnectors, String s) {
    if ("seq".equals(s)) {
      routeBinder.appendOutConnectorsSeq(outConnectors);
    } else if ("par".equals(s)) {
      routeBinder.appendOutConnectorsParallel(outConnectors);
    }
  }

  static CamelContext getCamelContext() {
    return camelContext;
  }

  void populateTestingRoute(InConnector connector, CentralRouter router) {
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
  }
}
