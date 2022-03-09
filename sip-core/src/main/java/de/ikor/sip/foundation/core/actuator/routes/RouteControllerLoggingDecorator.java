package de.ikor.sip.foundation.core.actuator.routes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteControllerLoggingDecorator {
  private final CamelContext ctx;

  public void startRoute(String routeId) throws Exception {
    ctx.getRouteController().startRoute(routeId);
    log.info("Started {} - ({})", routeId, getRouteEndpointUri(routeId));
  }

  public void stopRoute(String routeId) throws Exception {
    ctx.getRouteController().stopRoute(routeId);
  }

  public void suspendRoute(String routeId) throws Exception {
    ctx.getRouteController().suspendRoute(routeId);
  }

  public void resumeRoute(String routeId) throws Exception {
    ctx.getRouteController().resumeRoute(routeId);
    log.info("Resumed {} - ({})", routeId, getRouteEndpointUri(routeId));
  }

  private String getRouteEndpointUri(String routeId) {
    return ctx.getRoute(routeId).getEndpoint().getEndpointUri();
  }
}
