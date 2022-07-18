package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvokerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.camel.*;
import org.springframework.stereotype.Component;

/** Util class that executes a request to a certain route (defined in the Exchange) */
@Component
@RequiredArgsConstructor
public class SIPRouteProducerTemplate {

  private final RouteInvokerFactory routeInvokerFactory;
  private final SIPEndpointResolver sipEndpointResolver;

  /**
   * Request an exchange on camel route
   *
   * @param exchange {@link Exchange} that is sent to a route
   * @return {@link Exchange} result of request
   */
  public Exchange requestOnRoute(Exchange exchange) {
    Endpoint endpoint = sipEndpointResolver.resolveEndpoint(exchange);
    return routeInvokerFactory.resolveAndInvoke(exchange, endpoint);
  }
}
