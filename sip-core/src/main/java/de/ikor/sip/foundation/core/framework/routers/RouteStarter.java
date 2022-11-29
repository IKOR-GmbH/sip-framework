package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouterDefinition> availableRouters;

  public RouteStarter(List<CentralRouterDefinition> centralRouters) {
    this.availableRouters =
        centralRouters.stream()
            .filter(router -> router.getClass().isAnnotationPresent(CentralRouterDomainModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public void notify(CamelEvent event) {
    setStaticCamelContext(event);
    availableRouters.stream()
        .map(CentralRouterDefinition::toCentralRouter)
        .forEach(CentralRouter::setUpRoutes);
  }

  private void setStaticCamelContext(CamelEvent event) {
    CamelContext camelContext = ((CamelEvent.CamelContextInitializingEvent) event).getContext();
    StaticRouteBuilderHelper.setCamelContext(
            camelContext);
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
