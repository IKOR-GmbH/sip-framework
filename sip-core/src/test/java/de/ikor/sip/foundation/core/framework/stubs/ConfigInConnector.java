package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

public class ConfigInConnector extends InConnector {
  @Override
  public String getName() {
    return "Config conn";
  }

  @Override
  public void configure() {
    from(InEndpoint.instance("sipmc:config", "config-id"))
        .process(exchange -> exchange.getMessage());
  }
}
