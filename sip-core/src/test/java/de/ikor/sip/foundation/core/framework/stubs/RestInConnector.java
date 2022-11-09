package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.RestInEndpoint;

public class RestInConnector extends InConnector {
  @Override
  public String getName() {
    return "rest-connector";
  }

  @Override
  public void configure() {
    from(rest("/hello-append", "get-rest").get())

//    from(RestInEndpoint.instance("/hello-append", "get-rest").definition().get())
        .process(exchange -> exchange.getMessage().setBody("hello rest"));
  }

  @Override
  public void configureOnException() {}
}
