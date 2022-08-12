package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.testkit.util.SIPExchangeHelper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

/** Default Invoker class for default behaviour when specific RouteInvoker is missing */
@RequiredArgsConstructor
public class DefaultRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;

  @Override
  public Exchange invoke(Exchange exchange, Endpoint endpoint) {
    return SIPExchangeHelper.createEmptyExchange(camelContext);
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return false;
  }
}
