package de.ikor.sip.foundation.core.scope.exchange.notifiers;

import de.ikor.sip.foundation.core.scope.exchange.ExchangeContextHolder;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.impl.event.ExchangeFailedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

public class CompletedExchangeEventNotifier extends EventNotifierSupport {

    public void notify(CamelEvent event) {
        resetContextHolderInstance();
    }

    private void resetContextHolderInstance() {
        ExchangeContextHolder conversationContextHolder = ExchangeContextHolder.instance();
        conversationContextHolder.resetConversationAttributes();
    }

    @Override
    public boolean isEnabled(CamelEvent event) {
        return event instanceof ExchangeCompletedEvent || event instanceof ExchangeFailedEvent;
    }
}