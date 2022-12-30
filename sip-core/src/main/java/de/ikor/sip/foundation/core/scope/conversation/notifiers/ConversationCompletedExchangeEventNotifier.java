package de.ikor.sip.foundation.core.scope.conversation.notifiers;

import de.ikor.sip.foundation.core.scope.conversation.ConversationContextHolder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.AbstractExchangeEvent;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.impl.event.ExchangeFailedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class ConversationCompletedExchangeEventNotifier extends EventNotifierSupport {

  public void notify(CamelEvent event) {
    AbstractExchangeEvent abstractExchangeEvent = (AbstractExchangeEvent) event;
    Exchange exchange = abstractExchangeEvent.getExchange();
    String key =
        exchange.getProperty(ConversationCreatedExchangeEventNotifier.SCOPE_PROPERTY, String.class);
    ConversationContextHolder.instance().removeBreadcrumbs(key, exchange.getExchangeId());
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof ExchangeCompletedEvent || event instanceof ExchangeFailedEvent;
  }
}
