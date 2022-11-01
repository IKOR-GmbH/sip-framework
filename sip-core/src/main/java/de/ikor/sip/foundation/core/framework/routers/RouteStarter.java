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
  CamelContext camelContext;

  public RouteStarter(List<CentralRouter> availableRouters) {
    this.availableRouters = availableRouters;
  }

  @Override
  public void notify(CamelEvent event) {
    this.camelContext = ((CamelEvent.CamelContextInitializingEvent) event).getContext();
    CentralRouter.setCamelContext(camelContext);
    availableRouters.forEach(this::buildRoutes);
  }

  void buildRoutes(CentralRouter router) {
    CentralEndpointsRegister.setState("actual");
    try {
      router.configureOnException();
      router.configure();
      for (InConnector connector : router.getInConnectors()) {
        if (connector.getRegisteredInCamel()) {
          continue;
        }
        addRoutesFromConnector(connector);
        CentralEndpointsRegister.setState("testing");
        router.switchToTestingDefinitionMode(connector);
        addRoutesFromConnector(connector);
        CentralEndpointsRegister.setState("actual");
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

  private void addRoutesFromConnector(InConnector inConnector) throws Exception {
    if (inConnector.getRestBuilder() != null) {
      camelContext.addRoutes(inConnector.getRestBuilder());
    }
    camelContext.addRoutes(inConnector.getRouteBuilder());
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
