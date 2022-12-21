package de.ikor.sip.foundation.core.framework.beans.notifiers;

import de.ikor.sip.foundation.core.framework.beans.ConversationContextHolder;
import de.ikor.sip.foundation.core.framework.beans.ConversationScope;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.AbstractExchangeEvent;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.impl.event.ExchangeFailedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

public class CompletedExchangeEventNotifier extends EventNotifierSupport {

  public void notify(CamelEvent event) {
    resetContextHolderInstance();
  }

  private void resetContextHolderInstance() {
    ConversationContextHolder conversationContextHolder = ConversationContextHolder.instance();
    conversationContextHolder.resetConversationAttributes();
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return  event instanceof ExchangeCompletedEvent ||
            event instanceof ExchangeFailedEvent;
  }
}
