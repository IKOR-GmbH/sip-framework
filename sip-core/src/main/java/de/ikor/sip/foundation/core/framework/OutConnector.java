package de.ikor.sip.foundation.core.framework;

import org.apache.camel.model.RouteDefinition;

public abstract class OutConnector {

  public abstract String getName();
  public abstract RouteDefinition configure(RouteDefinition route);
}
