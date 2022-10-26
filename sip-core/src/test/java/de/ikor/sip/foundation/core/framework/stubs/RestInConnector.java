package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.InConnector;

public class RestInConnector extends InConnector {
  @Override
  public String getName() {
    return "rest-connector";
  }

  @Override
  public void configure() {
    from(rest("/hello-append", "get-rest").get())
        .process(exchange -> exchange.getMessage().setBody("hello rest"));
  }
}
