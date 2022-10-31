package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

public class OnExceptionInConnector extends InConnector {

    private final InEndpoint inEndpoint;

    public OnExceptionInConnector(InEndpoint inEndpoint) {
        this.inEndpoint = inEndpoint;
    }

    @Override
    public String getName() {
        return "OnExceptionInConnector";
    }

    @Override
    public void configure() {
        from(inEndpoint)
                .log("lets cause DummyException in InConnector")
                .process(exchange -> {
                    throw new TestingDummyException("fake exception");
                });
    }

    @Override
    public void configureOnException() {
        onException(TestingDummyException.class)
                .handled(true)
                .log("DummyException happened, InConnector onException handler is invoked!")
                .process(exchange -> {
                    exchange.getMessage().setBody("InConnectorException");
                })
                .to("log:message");
    }
}
