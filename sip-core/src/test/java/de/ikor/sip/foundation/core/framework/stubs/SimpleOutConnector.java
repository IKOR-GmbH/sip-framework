package de.ikor.sip.foundation.core.framework.stubs;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

public class SimpleOutConnector extends TestingOutConnector {

  public SimpleOutConnector() {
    // Constructor for testing purposes only
    super("just-a-testing-connector" + System.nanoTime());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void configure(RouteDefinition route) {
    route
        .setBody(exchange -> exchange.getIn().getBody() + format("-[%s]", super.endpointId))
        .multicast()
        .to(OutEndpoint.instance(uri, endpointId))
        .id("log-message-endpoint");
  }
}