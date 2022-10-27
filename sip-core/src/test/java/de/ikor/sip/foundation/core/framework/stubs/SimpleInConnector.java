package de.ikor.sip.foundation.core.framework.stubs;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.InConnector;
import de.ikor.sip.foundation.core.framework.InEndpoint;

public class SimpleInConnector extends InConnector {
  private InEndpoint ep;
  private String name = format("testing-connector-%s", System.nanoTime());

  private SimpleInConnector(String endpointUri, String id) {
    ep = InEndpoint.instance(endpointUri, id);
  }

  public static SimpleInConnector withUri(String endpointUri) {
    return new SimpleInConnector(endpointUri, format("in-ep-id-%s", System.nanoTime()));
  }

  @Override
  public void configure() {
    from(ep);
  }

  @Override
  public void configureOnConnectorLevel() {}

  public String getName() {
    return name;
  }
}
