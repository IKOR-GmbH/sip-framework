package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import org.apache.camel.model.RouteDefinition;

public class ConfigOutConnector extends OutConnectorDefinition {
  @Override
  public void configure(RouteDefinition route) {
    route.to("seda:out-config");
  }
}
