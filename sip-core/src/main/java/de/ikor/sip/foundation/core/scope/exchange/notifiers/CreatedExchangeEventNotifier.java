package de.ikor.sip.foundation.core.scope.exchange.notifiers;

import de.ikor.sip.foundation.core.scope.exchange.ExchangeContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.ExchangeCreatedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

@Slf4j
@RequiredArgsConstructor
public class CreatedExchangeEventNotifier extends EventNotifierSupport {

  public void notify(CamelEvent event) {
    ExchangeCreatedEvent ece = (ExchangeCreatedEvent) event;
    Exchange exchange = ece.getExchange();
    ExchangeContextHolder.instance().setConversationAttributes(exchange);
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof ExchangeCreatedEvent;
  }
}
