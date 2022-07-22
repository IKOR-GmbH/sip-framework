package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import de.ikor.sip.foundation.testkit.util.SIPEndpointResolver;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import java.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Factory class which invokes proper RouteInvoker based on Endpoint type */
@Component
public class RouteInvokerFactory {

  private final List<RouteInvoker> invokers;
  private final SIPEndpointResolver sipEndpointResolver;

  private final CamelContext camelContext;

  @Autowired
  public RouteInvokerFactory(
      Set<RouteInvoker> invokerSet,
      SIPEndpointResolver sipEndpointResolver,
      CamelContext camelContext) {
    this.sipEndpointResolver = sipEndpointResolver;
    this.camelContext = camelContext;
    invokers = new ArrayList<>();
    invokers.addAll(invokerSet);
  }

  public RouteInvoker getInstance(Exchange exchange) {
    Endpoint endpoint = sipEndpointResolver.resolveEndpoint(exchange);
    return invokers.stream()
        .filter(routeInvoker -> routeInvoker.matchEndpoint(endpoint))
        .findFirst()
        .map(routeInvoker -> routeInvoker.setEndpoint(endpoint))
        .orElse(new DefaultRouteInvoker(camelContext, endpoint));
  }
}
