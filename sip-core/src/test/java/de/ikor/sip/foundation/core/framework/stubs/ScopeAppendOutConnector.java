package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.beans.CDMHolder;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScopeAppendOutConnector extends OutConnector {
    @Autowired
    CDMHolder bean;

    @Override
    public void configure(RouteDefinition route) {
        route.process(
                exchange -> {
                    String body = bean.getInternal() + "-bean";
                    exchange.getMessage().setBody(body);
                });
    }
}
