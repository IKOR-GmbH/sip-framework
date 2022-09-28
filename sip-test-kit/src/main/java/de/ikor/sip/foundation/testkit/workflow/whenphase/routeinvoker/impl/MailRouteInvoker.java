package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.resolveConsumer;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.mail.MailConsumer;
import org.apache.camel.component.mail.MailEndpoint;
import org.apache.camel.support.EmptyAsyncCallback;
import org.springframework.stereotype.Component;

/** Invoker of processing exchanges for {@link MailConsumer} */
@Component
@RequiredArgsConstructor
public class MailRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;

  @Override
  public Optional<Exchange> invoke(Exchange exchange) {
    MailConsumer mailConsumer = (MailConsumer) resolveConsumer(exchange, camelContext);
    mailConsumer.getAsyncProcessor().process(exchange, EmptyAsyncCallback.get());
    return Optional.empty();
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof MailEndpoint;
  }
}
