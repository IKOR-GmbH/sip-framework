package de.ikor.sip.foundation.core.framework.beans;

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
public class ExchangeEventNotifier extends EventNotifierSupport {
    private final Map<String, ExchangeAttributes> cdms= new HashMap<>();


    public void notify(CamelEvent event) {
        if(event instanceof ExchangeCreatedEvent) {

            ExchangeCreatedEvent ece = (ExchangeCreatedEvent) event;

            Exchange exchange = ece.getExchange();

            if(exchange.getProperty(ExchangeScope.SCOPE_PROPERTY) == null) {
                exchange.setProperty(ExchangeScope.SCOPE_PROPERTY, exchange.getExchangeId());
            }
            log.debug("RECEIVED CREATE EVENT - " + exchange.getProperty(ExchangeScope.SCOPE_PROPERTY));
            ExchangeAttributes exchangeAttributes =
                    cdms.getOrDefault(exchange.getProperty(ExchangeScope.SCOPE_PROPERTY, String.class),
                            new ExchangeAttributes(exchange));
            cdms.put(exchange.getProperty(ExchangeScope.SCOPE_PROPERTY, String.class), exchangeAttributes);
            ExchangeContextHolder.instance().setContext(exchangeAttributes);
        }

        if(event instanceof ExchangeCompletedEvent || event instanceof ExchangeFailedEvent) {
            ExchangeCompletedEvent ece = (ExchangeCompletedEvent) event;

            Exchange exchange = ece.getExchange();
            log.debug("RECEIVED COMPLETE EVENT - " + exchange.getProperty(ExchangeScope.SCOPE_PROPERTY));
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
