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
      router.configureOnCentralRouterLevel();
      router.configure();
      router.buildOutgoingConnector(); // moguce da se ovo moze izbaciti
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
