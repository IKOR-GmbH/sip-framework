package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.OutConnector;
import de.ikor.sip.foundation.core.framework.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

import static java.lang.String.format;


public class SimpleOutConnector extends OutConnector {
  private String endpointId = "endpoint-id";
  private final String name;
  private String uri = "log:message";

  // Constructor for testing purposes only
  public SimpleOutConnector() {
    name = "just-a-testing-connector" + System.nanoTime();
  }

  // Constructor for testing purposes only
  public SimpleOutConnector(String outEndpointId) {
    endpointId = outEndpointId;
    name = "just-a-testing-connector" + System.nanoTime();
  }
  // Constructor for testing purposes only
  public SimpleOutConnector(String outEndpointUri, String outEndpointId) {
    this.endpointId = outEndpointId;
    this.uri = outEndpointUri;
    name = "just-a-testing-connector" + System.nanoTime();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public RouteDefinition configure(RouteDefinition route) {
    return route
            .setBody(exchange -> exchange.getIn().getBody() + format("-[%s]", endpointId))
            .to(OutEndpoint.instance(uri, endpointId)).id("log-message-endpoint");
  }
}
