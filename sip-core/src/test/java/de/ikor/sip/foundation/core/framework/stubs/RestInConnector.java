package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestInConnector extends InConnectorDefinition {
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
