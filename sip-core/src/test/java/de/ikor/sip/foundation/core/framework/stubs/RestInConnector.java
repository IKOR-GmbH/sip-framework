package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestInConnector extends InConnector {
  final private String path;
  @Override
  public String getName() {
    return "rest-connector";
  }

  @Override
  public void configure() {
    from(rest(path, "get-rest").get())
        .process(exchange -> exchange.getMessage().setBody("hello rest"));
  }

  @Override
  public void configureOnException() {}
}
