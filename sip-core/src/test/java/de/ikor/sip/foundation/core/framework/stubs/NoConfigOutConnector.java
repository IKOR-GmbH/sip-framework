package de.ikor.sip.foundation.core.framework.stubs;

import org.apache.camel.Exchange;
import org.apache.camel.model.RouteDefinition;

import java.util.Objects;

import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_KEY;

public class NoConfigOutConnector extends TestingOutConnector {
  public NoConfigOutConnector(String name) {
    super(name);
  }

  @Override
  public void configure(RouteDefinition route) {
    route.
    setBody(this::bodyFromHeader)
    .to("seda:out-" + name);
  }

  private String bodyFromHeader(Exchange exchange) {
    Object header = exchange.getMessage().getHeader(SCENARIO_HEADER_KEY);
    return name + " " + Objects.toString(header, "");
  }
}
