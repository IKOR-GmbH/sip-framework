package de.ikor.sip.foundation.core.framework.stubs;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

public class SleepingOutConnector extends TestingOutConnector {

  public SleepingOutConnector() {
    super("sleeping-connector-" + System.nanoTime());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void configure(RouteDefinition route) {
    route
        .process(exchange -> Thread.sleep(1000)) // TODO handle Thread.sleep if possible
        .setBody(exchange -> exchange.getIn().getBody() + format("-[%s]", endpointId))
        .to(OutEndpoint.instance(uri, endpointId))
        .id("log-message-endpoint");
  }
}
