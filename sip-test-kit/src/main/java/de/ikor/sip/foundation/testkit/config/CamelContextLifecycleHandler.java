package de.ikor.sip.foundation.testkit.config;

import de.ikor.sip.foundation.testkit.exception.UnsuspendedRouteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Route;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.support.ScheduledPollConsumer;
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

  @Override
  public void beforeApplicationStart(CamelContext camelContext) {
    // Do nothing before starting camel context
  }

  @Override
  public void afterApplicationStart(CamelContext camelContext) {
    suspendRoutes(camelContext);
  }

  private void suspendRoutes(CamelContext camelContext) {
    camelContext.getRoutes().forEach(route -> checkAndSuspend(route, camelContext));
  }

  private void checkAndSuspend(Route route, CamelContext camelContext) {
    Consumer consumer = route.getConsumer();
    if (consumer instanceof PollingConsumer || consumer instanceof ScheduledPollConsumer) {
      suspendRoute(route.getRouteId(), camelContext);
    }
  }

  private void suspendRoute(String routeId, CamelContext camelContext) {
    try {
      camelContext.getRouteController().suspendRoute(routeId);
    } catch (Exception e) {
      camelContext.stop();
      log.debug("sip.testkit.config.nosuspendingroute_{}", routeId);
      throw new UnsuspendedRouteException(routeId);
    }
  }
}
