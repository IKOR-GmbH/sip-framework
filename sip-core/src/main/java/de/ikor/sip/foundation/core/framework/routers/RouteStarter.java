package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouter> availableRouters;

  public RouteStarter(
          List<CentralRouterDefinition> centralRouters,
          Optional<GlobalRoutesConfiguration> globalRoutesConfiguration) {
    this.availableRouters =
            centralRouters.stream()
                    .filter(router -> router.getClass().isAnnotationPresent(IntegrationScenario.class))
                    .map(CentralRouterDefinition::toCentralRouter)
                    .collect(Collectors.toList());
    globalRoutesConfiguration.ifPresent(GlobalRoutesConfiguration::defineGlobalConfiguration);
    availableRouters.forEach(router -> router.appendToRouteConfig(globalRoutesConfiguration));
  }

  @Override
  public void notify(CamelEvent event) {
    setStaticCamelContext(event);
    availableRouters.forEach(CentralRouter::setUpRoutes);
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
