package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import org.apache.camel.model.RouteDefinition;

public class OnExceptionOutConnector extends OutConnector {

  private final OutEndpoint outEndpoint;

  public OnExceptionOutConnector(OutEndpoint outEndpoint) {
    this.outEndpoint = outEndpoint;
  }

  @Override
  public void configure(RouteDefinition route) {
    route
        .log("lets cause DummyException in OutConnector")
        .process(
            exchange -> {
              throw new TestingDummyException("fake exception");
            })
        .to(outEndpoint.getEndpointUri());
  }

  @Override
  public void configureOnException() {
    onException(TestingDummyException.class)
        .handled(true)
        .log("DummyException happened, OutConnector onException handler is invoked!")
        .process(
            exchange -> {
              exchange.getMessage().setBody("OutConnectorException");
            })
        .to("log:message");
  }
}
