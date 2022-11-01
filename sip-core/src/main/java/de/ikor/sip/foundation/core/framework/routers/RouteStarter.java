package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  List<CentralRouter> availableRouters;

  private static CamelContext camelContext;

  public RouteStarter(List<CentralRouter> availableRouters) {
    this.availableRouters = availableRouters;
  }

  @Override
  public void notify(CamelEvent event) {
    camelContext = ((CamelEvent.CamelContextInitializingEvent) event).getContext();
    CentralEndpointsRegister.setCamelContext(camelContext);
    availableRouters.forEach(this::buildRoutes);
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }

  void buildRoutes(CentralRouter router) {
    try {
      router.configureOnException();
      router.configure();

      for (InConnector connector : router.getInConnectors()) {
        if (Boolean.TRUE.equals(connector.getRegisteredInCamel())) {
          continue;
        }
        CentralEndpointsRegister.putInTestingState();
        router.populateTestingRoute(connector);
        CentralEndpointsRegister.putInActualState();
        camelContext.addRoutes(connector.getRouteBuilder());
        connector.setRegisteredInCamel(true);
      }

      if (router.getUseCaseDefinition() != null) {

        UseCaseTopologyDefinition useCaseTopologyDefinition = router.getUseCaseDefinition();
        camelContext.addRoutes(useCaseTopologyDefinition.getRouteBuilder());
        camelContext.addRoutes(useCaseTopologyDefinition.getTestingRouteBuilder());

        for (RouteBuilder rb : useCaseTopologyDefinition.getOutConnectorsRouteBuilders()) {
          camelContext.addRoutes(rb);
        }
      } else {
        throw new EmptyCentralRouterException(router.getScenario());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static CamelContext getCamelContext() {
    return camelContext;
  }
}
