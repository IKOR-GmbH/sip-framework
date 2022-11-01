package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import org.apache.camel.model.RouteDefinition;

public class ConfigOutConnector extends OutConnector {
  @Override
  public void configure(RouteDefinition route) {
    route.to("seda:out-config");
  }
}
