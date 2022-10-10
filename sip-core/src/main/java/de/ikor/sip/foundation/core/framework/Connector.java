package de.ikor.sip.foundation.core.framework;

import org.apache.camel.model.RouteDefinition;

public abstract class Connector {
    public abstract String getName();

    public void handleResponse(RouteDefinition route) {

    }
}
