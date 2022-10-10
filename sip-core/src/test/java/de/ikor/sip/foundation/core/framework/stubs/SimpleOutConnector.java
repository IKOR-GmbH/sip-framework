package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

import static java.lang.String.format;

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
        .setBody(exchange -> exchange.getIn().getBody() + format("-[%s]", endpointId))
        .multicast()
        .to(OutEndpoint.instance(uri, endpointId))
        .id("log-message-endpoint");
  }
}
