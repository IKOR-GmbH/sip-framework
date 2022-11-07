package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import lombok.AllArgsConstructor;
import org.apache.camel.model.RouteDefinition;

@AllArgsConstructor
public class SimpleBodyOutConnector extends OutConnector {

  private String name;
  private String body;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void configure(RouteDefinition route) {
    route.process(exchange -> exchange.getMessage().setBody(body));
  }
}
