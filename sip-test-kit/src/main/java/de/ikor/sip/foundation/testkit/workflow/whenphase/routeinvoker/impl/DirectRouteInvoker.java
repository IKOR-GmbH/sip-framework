package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.direct.DirectEndpoint;
import org.springframework.stereotype.Component;

/**
 * Invoker class for triggering routes with Direct consumer. Used for tests which are being started
 * with connector id
 */
@Component
@RequiredArgsConstructor
public class DirectRouteInvoker implements RouteInvoker {

  private final ProducerTemplate producerTemplate;

  private final CamelContext camelContext;

  @Override
  public Optional<Exchange> invoke(Exchange inputExchange) {
    Endpoint endpoint = TestKitHelper.resolveEndpoint(inputExchange, camelContext);
    Exchange exchange = producerTemplate.send(endpoint, inputExchange);
    return Optional.of(exchange);
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof DirectEndpoint;
  }
}
