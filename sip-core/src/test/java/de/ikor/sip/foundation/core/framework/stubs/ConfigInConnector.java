package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

public class ConfigInConnector extends InConnectorDefinition {
  @Override
  public String getName() {
    return "Config conn";
  }

  @Override
  public void configure() {
    from(InEndpoint.instance("seda:config", "config-id"))
        .process(exchange -> exchange.getMessage());
  }
}
