package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;

public abstract class TestingOutConnector extends OutConnector {
  protected final String name;
  protected String endpointId = "endpoint-id";
  protected String uri = "log:message";

  protected TestingOutConnector(String name) {
    this.name = name;
  }

  public <T extends TestingOutConnector> T withId(String outEndpointId) {
    this.endpointId = outEndpointId;
    return (T) this;
  }

  public <T extends TestingOutConnector> T withUri(String outEndpointUri) {
    this.uri = outEndpointUri;
    return (T) this;
  }
}
