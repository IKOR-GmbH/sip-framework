package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;

@RequiredArgsConstructor
public class DefaultRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;
  private final Endpoint endpoint;

  @Override
  public Exchange invoke(Exchange exchange) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }

  @Override
  public boolean isEndpoint(Endpoint endpoint) {
    return false;
  }

  @Override
  public RouteInvoker setEndpoint(Endpoint endpoint) {
    return this;
  }
}
