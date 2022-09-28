package de.ikor.sip.foundation.testkit.config;

import de.ikor.sip.foundation.testkit.exception.UnsuspendedRouteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Route;
import org.apache.camel.component.jms.JmsConsumer;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.support.ScheduledPollConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Config class for handling Camel Context by using its lifecycle methods. Used only in SIP batch
 * tests.
 */
@Slf4j
@ConditionalOnProperty(name = "sip.testkit.batch-test", havingValue = "true")
@Component
public class CamelContextLifecycleHandler implements CamelContextConfiguration {

  @Override
  public void beforeApplicationStart(CamelContext camelContext) {
    // Do nothing before starting camel context
  }

  @Override
  public void afterApplicationStart(CamelContext camelContext) {
    suspendPollingConsumerRoutes(camelContext);
  }

  private void suspendPollingConsumerRoutes(CamelContext camelContext) {
    camelContext.getRoutes().forEach(route -> checkAndSuspend(route, camelContext));
  }

  private void checkAndSuspend(Route route, CamelContext camelContext) {
    Consumer consumer = route.getConsumer();
    if (isSuspendingConsumer(consumer)) {
      suspendRoute(route.getRouteId(), camelContext);
    }
  }

  private boolean isSuspendingConsumer(Consumer consumer) {
    return consumer instanceof PollingConsumer
        || consumer instanceof ScheduledPollConsumer
        || consumer instanceof JmsConsumer;
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
