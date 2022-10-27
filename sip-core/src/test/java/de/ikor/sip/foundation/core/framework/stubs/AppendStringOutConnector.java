package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import org.apache.camel.model.RouteDefinition;

public class AppendStringOutConnector extends OutConnector {
  @Override
  public void configure(RouteDefinition route) {
    route.process(
        exchange ->
            exchange.getMessage().setBody(exchange.getMessage().getBody(String.class) + "-append"));
  }

  @Override
  public void configureOnConnectorLevel() {}
}
