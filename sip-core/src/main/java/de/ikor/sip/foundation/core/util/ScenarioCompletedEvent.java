package de.ikor.sip.foundation.core.util;

import org.apache.camel.Exchange;
import org.apache.camel.impl.event.AbstractExchangeEvent;

public class ScenarioCompletedEvent extends AbstractExchangeEvent {

    public ScenarioCompletedEvent(Exchange source) {
        super(source);
    }

    @Override
    public Type getType() {
        return Type.Custom;
    }
}
