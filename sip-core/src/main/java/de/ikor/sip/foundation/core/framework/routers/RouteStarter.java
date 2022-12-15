package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import de.ikor.sip.foundation.core.framework.configurations.GlobalRoutesConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouterService> availableRouters;

  public RouteStarter(
      List<CentralRouter> centralRouters,
      Optional<GlobalRoutesConfiguration> globalRoutesConfiguration) {
    this.availableRouters =
        centralRouters.stream()
            .filter(router -> router.getClass().isAnnotationPresent(IntegrationScenario.class))
            .map(CentralRouter::toCentralRouter)
            .collect(Collectors.toList());
    globalRoutesConfiguration.ifPresent(GlobalRoutesConfiguration::defineGlobalConfiguration);
    availableRouters.forEach(router -> router.appendToRouteConfig(globalRoutesConfiguration));
  }

  @Override
  public void notify(CamelEvent event) {
    setStaticCamelContext(event);
    availableRouters.forEach(CentralRouterService::setUpRoutes);
  }

  private void setStaticCamelContext(CamelEvent event) {
    CamelContext camelContext = ((CamelEvent.CamelContextInitializingEvent) event).getContext();
    StaticRouteBuilderHelper.setCamelContext(camelContext);
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
