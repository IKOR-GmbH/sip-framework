package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import java.util.List;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  List<CentralRouter> availableRouters;
  Optional<AdapterRouteConfiguration> routeConfiguration;
  CamelContext camelContext;

  public RouteStarter(
      List<CentralRouter> availableRouters,
      Optional<AdapterRouteConfiguration> routeConfiguration) {
    this.availableRouters = availableRouters;
    this.routeConfiguration = routeConfiguration;
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
      routeConfiguration.ifPresent(AdapterRouteConfiguration::globalConfiguration);
      routeConfiguration.ifPresent(router::addConfigToRouteBuilder);
      router.scenarioConfiguration();
      router.configureOnException();
      router.configure();
      for (InConnector connector : router.getInConnectors()) {
        addRoutesFromConnector(connector);
        CentralEndpointsRegister.setState("testing");
        router.switchToTestingDefinitionMode(connector);
        addRoutesFromConnector(connector);
        CentralEndpointsRegister.setState("actual");
        connector.setRegisteredInCamel(true);
      }
      if (router.getUseCaseDefinition() != null) {
        router.getUseCaseDefinition().build();
      } else {
        throw new EmptyCentralRouterException(router.getScenario());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addRoutesFromConnector(InConnector inConnector) throws Exception {
    if (inConnector.getRegisteredInCamel()) {
      return;
    }
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
