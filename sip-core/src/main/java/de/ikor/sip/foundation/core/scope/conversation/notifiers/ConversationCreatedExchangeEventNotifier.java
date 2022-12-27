package de.ikor.sip.foundation.core.scope.conversation.notifiers;

import de.ikor.sip.foundation.core.scope.conversation.ConversationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.ExchangeCreatedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

@Slf4j
@RequiredArgsConstructor
public class ConversationCreatedExchangeEventNotifier extends EventNotifierSupport {
  public static final String SCOPE_PROPERTY = "scopeKey";

  public void notify(CamelEvent event) {
    ExchangeCreatedEvent ece = (ExchangeCreatedEvent) event;
    Exchange exchange = ece.getExchange();
    String key = exchange.getProperty(SCOPE_PROPERTY, String.class);
    if (key == null) {
      key = exchange.getExchangeId();
      exchange.setProperty(SCOPE_PROPERTY, key);
    }
    ConversationContextHolder.instance().setConversationAttributes(key);
    ConversationContextHolder.instance().appendBreadcrumbs(key, exchange.getExchangeId());
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof ExchangeCreatedEvent;
  }
}
