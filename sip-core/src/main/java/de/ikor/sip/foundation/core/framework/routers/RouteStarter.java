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
  final List<CentralRouterDefinition> availableRouters;
  private final Optional<GlobalRoutesConfiguration> routeConfiguration;

  public RouteStarter(
          List<CentralRouterDefinition> centralRouters,
          Optional<GlobalRoutesConfiguration> routeConfiguration) {
    this.availableRouters =
            centralRouters.stream()
                    .filter(router -> router.getClass().isAnnotationPresent(CentralRouterDomainModel.class))
                    .collect(Collectors.toList());
    this.routeConfiguration = routeConfiguration;
    this.routeConfiguration.ifPresent(GlobalRoutesConfiguration::defineGlobalConfiguration);
  }

  @Override
  public void notify(CamelEvent event) {
    setStaticCamelContext(event);
    List<CentralRouter> centralRouterStream = availableRouters.stream()
            .map(CentralRouterDefinition::toCentralRouter).collect(Collectors.toList());

    centralRouterStream.forEach(router -> router.setGlobalRouteConfig(routeConfiguration));
    centralRouterStream.forEach(CentralRouter::setUpRoutes);
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
