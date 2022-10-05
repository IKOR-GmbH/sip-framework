package de.ikor.sip.foundation.core.framework;

import org.apache.camel.model.RouteDefinition;

public abstract class OutConnector {

  public String getName() {
    return this.getClass().getSimpleName();
  }
  public abstract void configure(RouteDefinition route);
}
