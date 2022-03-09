package de.ikor.sip.foundation.core.actuator.routes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
/**
 * Class used by {@link RouteOperation} instead of calling {@link org.apache.camel.spi.RouteController}, in
 * order to add missing logs.
 */
public class RouteControllerLoggingDecorator {
  private final CamelContext ctx;

  /**
   * Starting route and logging the action
   * @param routeId - Id of route to be started
   * @throws Exception
   */
  public void startRoute(String routeId) throws Exception {
    ctx.getRouteController().startRoute(routeId);
    log.info("Started {} - ({})", routeId, getRouteEndpointUri(routeId));
  }

  /**
   * Stopping route
   * @param routeId - Id of route to be stopped
   * @throws Exception
   */
  public void stopRoute(String routeId) throws Exception {
    ctx.getRouteController().stopRoute(routeId);
  }

  /**
   * Suspending route
   * @param routeId - Id of route to be suspended
   * @throws Exception
   */
  public void suspendRoute(String routeId) throws Exception {
    ctx.getRouteController().suspendRoute(routeId);
  }

  /**
   * Resuming route and logging the action
   * @param routeId - Id of route to be resumed
   * @throws Exception
   */
  public void resumeRoute(String routeId) throws Exception {
    ctx.getRouteController().resumeRoute(routeId);
    log.info("Resumed {} - ({})", routeId, getRouteEndpointUri(routeId));
  }

  private String getRouteEndpointUri(String routeId) {
    return ctx.getRoute(routeId).getEndpoint().getEndpointUri();
  }
}
