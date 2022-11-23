package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;

public abstract class TestingOutConnector extends OutConnectorDefinition {
  protected final String name;
  protected String endpointId = "endpoint-id";
  protected String uri = "log:message";

  protected TestingOutConnector(String name) {
    this.name = name;
  }

  public <T extends TestingOutConnector> T outEndpointId(String outEndpointId) {
    this.endpointId = outEndpointId;
    return (T) this;
  }

  public <T extends TestingOutConnector> T outEndpointUri(String outEndpointUri) {
    this.uri = outEndpointUri;
    return (T) this;
  }
}
