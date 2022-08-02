package de.ikor.sip.foundation.testkit.config;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Config class for handling Camel Context by using its lifecycle methods. Used only in SIP batch
 * tests.
 */
@Slf4j
@ConditionalOnProperty(name = "sip.testkit.batchTest", havingValue = "true")
@Component
@RequiredArgsConstructor
public class CamelContextLifecycleHandler implements CamelContextConfiguration {

  private final List<RouteInvoker> routeInvokers;

  @Override
  public void beforeApplicationStart(CamelContext camelContext) {}

  @Override
  public void afterApplicationStart(CamelContext camelContext) {
    suspendRoutes(camelContext);
  }

  private void suspendRoutes(CamelContext camelContext) {
    camelContext.getRoutes().forEach(route -> checkForSuspending(route, camelContext));
  }

  private void checkForSuspending(Route route, CamelContext camelContext) {
    routeInvokers.forEach(
        invoker -> {
          if (invoker.isSuspendable() && invoker.isApplicable(route.getEndpoint())) {
            suspendRoute(route.getRouteId(), camelContext);
          }
        });
  }

  private void suspendRoute(String routeId, CamelContext camelContext) {
    try {
      camelContext.getRouteController().suspendRoute(routeId);
    } catch (Exception e) {
      log.debug("sip.testkit.config.nosuspendingroute_{}", routeId);
    }
  }
}
