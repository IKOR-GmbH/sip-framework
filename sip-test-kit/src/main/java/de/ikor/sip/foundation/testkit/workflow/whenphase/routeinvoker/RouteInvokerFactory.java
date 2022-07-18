package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Factory class which invokes proper RouteInvoker based on Endpoint type */
@Component
public class RouteInvokerFactory {

  private List<RouteInvoker> invokers;

  private final CamelContext camelContext;

  @Autowired
  public RouteInvokerFactory(Set<RouteInvoker> invokerSet, CamelContext camelContext) {
    createInvokers(invokerSet);
    this.camelContext = camelContext;
  }

  /**
   * Resolving and invoking appropriate RouteInvoker based on Endpoint type
   *
   * @param endpoint {@link Endpoint} that is route consumer
   * @return {@link Exchange} Exchange which is route result
   */
  public Exchange resolveAndInvoke(Exchange inputExchange, Endpoint endpoint) {
    AtomicReference<Exchange> result = new AtomicReference<>(createEmptyExchange());
    invokers.forEach(
        invoker -> {
          if (invoker.matchEndpoint(endpoint)) {
            result.set(invoker.invoke(inputExchange, endpoint));
          }
        });
    return result.get();
  }

  private void createInvokers(Set<RouteInvoker> invokerSet) {
    invokers = new ArrayList<>();
    invokers.addAll(invokerSet);
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
