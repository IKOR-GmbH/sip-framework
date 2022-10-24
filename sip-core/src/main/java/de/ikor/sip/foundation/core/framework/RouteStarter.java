package de.ikor.sip.foundation.core.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  List<CentralRouter> availableRouters = new ArrayList<>();

  public RouteStarter(Optional<List<CentralRouter>> availableRouters) {
    availableRouters.ifPresent(centralRouters -> this.availableRouters = centralRouters);
  }

  @Override
  public void notify(CamelEvent event) {
    CentralRouter.setCamelContext(((CamelEvent.CamelContextInitializingEvent) event).getContext());
    availableRouters.forEach(this::buildRoutes);
  }

  private void buildRoutes(CentralRouter router) {
    try {
      router.configure();
      router.buildOutgoingConnector();
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO implement or reuse existing exception
    }
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
