package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.RouteProducer;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

/** Default class for creating empty exchange */
@Component
@RequiredArgsConstructor
public class DefaultRouteProducer implements RouteProducer {

  private final CamelContext camelContext;

  /**
   * Return exchange with empty body
   *
   * @param exchange {@link Exchange}
   * @param endpoint {@link Endpoint}
   * @return Exchange
   */
  @Override
  public Exchange executeTask(Exchange exchange, Endpoint endpoint) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
