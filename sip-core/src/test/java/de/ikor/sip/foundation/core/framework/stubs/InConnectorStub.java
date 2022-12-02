package de.ikor.sip.foundation.core.framework.stubs;

import static org.apache.camel.builder.Builder.body;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InConnectorStub extends InConnector {

  private InEndpoint inEndpoint;

  @Override
  public String getName() {
    return "BasicInConnector";
  }

  @Override
  public void configure() {
    from(inEndpoint).to("log:messageIn").setBody(body().convertToString());
  }
}
