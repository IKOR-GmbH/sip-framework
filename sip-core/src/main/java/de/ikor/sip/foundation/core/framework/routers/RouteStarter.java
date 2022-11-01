package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import java.util.List;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  List<CentralRouter> availableRouters;

  private static CamelContext camelContext;
  Optional<AdapterRouteConfiguration> routeConfiguration;

  public RouteStarter(
      List<CentralRouter> availableRouters,
      Optional<AdapterRouteConfiguration> routeConfiguration) {
    this.availableRouters = availableRouters;
    this.routeConfiguration = routeConfiguration;
    this.routeConfiguration.ifPresent(AdapterRouteConfiguration::globalConfiguration);
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
    CentralEndpointsRegister.setState("actual");
    try {
      routeConfiguration.ifPresent(router::addConfigToRouteBuilder);
      router.scenarioConfiguration();
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

  static CamelContext getCamelContext() {
    return camelContext;
  }

  private void addRoutesFromConnector(InConnector inConnector) throws Exception {
    camelContext.addRoutes(inConnector.getRouteBuilder());
  }
}
