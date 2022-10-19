package de.ikor.sip.foundation.core.framework;

import java.util.List;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RouteStarter extends EventNotifierSupport {
  @Autowired List<CentralRouter> availableRouters;

  @Override
  public void notify(CamelEvent event) throws Exception {
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
