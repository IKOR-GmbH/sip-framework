package de.ikor.sip.foundation.core.framework.beans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.impl.event.ExchangeCreatedEvent;
import org.apache.camel.impl.event.ExchangeFailedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExchangeEventNotifier extends EventNotifierSupport {

  public void notify(CamelEvent event) {
    if (event instanceof ExchangeCreatedEvent) {

      ExchangeCreatedEvent ece = (ExchangeCreatedEvent) event;
      Exchange exchange = ece.getExchange();
      String key = exchange.getProperty(ConversationScope.SCOPE_PROPERTY, String.class);
      if (key == null) {
        key = exchange.getExchangeId();
        exchange.setProperty(ConversationScope.SCOPE_PROPERTY, key);
      }
      ConversationContextHolder.instance().setConversationAttributes(key);
    } else {
      resetContextHolderInstance();
    }
  }

  private void resetContextHolderInstance() {
    ConversationContextHolder conversationContextHolder = ConversationContextHolder.instance();
    String conversationId = conversationContextHolder.getConversationId();
    if (conversationId != null) {
      conversationContextHolder.getScope().remove(conversationId);
    }
    conversationContextHolder.resetConversationAttributes();
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof ExchangeCreatedEvent
        || event instanceof ExchangeCompletedEvent
        || event instanceof ExchangeFailedEvent;
  }
}
