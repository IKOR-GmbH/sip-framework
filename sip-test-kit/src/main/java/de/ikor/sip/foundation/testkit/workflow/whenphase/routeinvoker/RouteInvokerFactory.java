package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

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
  private final CamelContext camelContext;

  @Autowired
  public RouteInvokerFactory(Set<RouteInvoker> invokerSet, CamelContext camelContext) {
    this.camelContext = camelContext;
    invokers = new ArrayList<>();
    invokers.addAll(invokerSet);
  }

  /**
   * Factory method which resolves RouteInvoker for proper camel Endpoint/Component
   *
   * @param exchange Exchange with route id
   * @return proper instance of RouteInvoker
   */
  public RouteInvoker getInstance(Exchange exchange, Endpoint endpoint) {
    return invokers.stream()
        .filter(routeInvoker -> routeInvoker.isApplicable(endpoint))
        .findFirst()
        .orElse(new DefaultRouteInvoker(camelContext));
  }
}
