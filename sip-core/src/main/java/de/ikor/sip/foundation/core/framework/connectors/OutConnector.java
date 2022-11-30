package de.ikor.sip.foundation.core.framework.connectors;

import org.apache.camel.model.RouteDefinition;

public abstract class OutConnector extends Connector {

  public abstract void configure(RouteDefinition route);

  public String getName() {
    return this.getClass().getSimpleName();
  }
}
