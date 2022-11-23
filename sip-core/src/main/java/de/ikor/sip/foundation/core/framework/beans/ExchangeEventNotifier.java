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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExchangeEventNotifier extends EventNotifierSupport {

    private final CDMRepository repository;

    public void notify(CamelEvent event) {
        if(event instanceof ExchangeCreatedEvent) {

            ExchangeCreatedEvent ece = (ExchangeCreatedEvent) event;

            Exchange exchange = ece.getExchange();
            String key = exchange.getProperty(ExchangeScope.SCOPE_PROPERTY, String.class);

            if(key == null) {
                exchange.setProperty(ExchangeScope.SCOPE_PROPERTY, exchange.getExchangeId());
            }
            log.debug("RECEIVED CREATE EVENT - " + exchange.getProperty(ExchangeScope.SCOPE_PROPERTY));
            ExchangeAttributes exchangeAttributes = new ExchangeAttributes(exchange);
            repository.put(key, exchangeAttributes);
            ExchangeContextHolder.instance().setContext((ExchangeAttributes) repository.getLast(key));
        }

        if(event instanceof ExchangeCompletedEvent || event instanceof ExchangeFailedEvent) {
            ExchangeCompletedEvent ece = (ExchangeCompletedEvent) event;

            Exchange exchange = ece.getExchange();
            log.debug("RECEIVED COMPLETE EVENT - " + exchange.getProperty(ExchangeScope.SCOPE_PROPERTY));
            if (ExchangeContextHolder.instance().getContext() != null)
                ExchangeContextHolder.instance().getContext().executeDestructionCallbacks();
            ExchangeContextHolder.instance().resetContext();
        }

    }

    @Override
    public boolean isEnabled(CamelEvent event) {
        return (
                (event instanceof ExchangeCreatedEvent) ||
                        (event instanceof ExchangeCompletedEvent)
        );
    }

    @Override
    protected void doStart() {
        setIgnoreCamelContextEvents(true);
        setIgnoreExchangeCompletedEvent(false);
        setIgnoreExchangeCreatedEvent(false);
        setIgnoreExchangeEvents(false);
        setIgnoreExchangeFailedEvents(true);
        setIgnoreExchangeRedeliveryEvents(true);
        setIgnoreExchangeSendingEvents(true);
        setIgnoreExchangeSentEvents(true);
        setIgnoreRouteEvents(true);
        setIgnoreServiceEvents(true);
    }
}
