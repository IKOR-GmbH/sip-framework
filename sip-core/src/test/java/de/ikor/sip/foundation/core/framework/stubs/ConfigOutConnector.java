package de.ikor.sip.foundation.core.framework.stubs;

import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_KEY;

public class ConfigOutConnector extends TestingOutConnector {
  public ConfigOutConnector(String name) {
    super(name);
  }

  @Override
  public void configure(RouteDefinition route) {
    route.
    setBody(exchange -> name + " "+ exchange.getMessage().getHeader(SCENARIO_HEADER_KEY))
    .to("seda:out-" + name);
  }
}
