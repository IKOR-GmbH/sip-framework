package de.ikor.sip.foundation.core.framework.stubs;

import static java.lang.String.format;
import static org.apache.camel.builder.Builder.body;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;

import java.util.function.Function;

public class SimpleInConnector extends InConnector {
  private InEndpoint ep;
  private String name = format("testing-connector-%s", System.nanoTime());

  private SimpleInConnector(String endpointUri, String id) {
    ep = InEndpoint.instance(endpointUri, id);
  }

  private SimpleInConnector(String endpointUri, String id, Class<?> endpointDomain, Function transformFunction) {
    ep = InEndpoint.instance(endpointUri, id, endpointDomain, transformFunction);
  }

  public static SimpleInConnector withUri(String endpointUri) {
    return new SimpleInConnector(endpointUri, format("in-ep-id-%s", System.nanoTime()));
  }

  public static SimpleInConnector withEndpoint(InEndpoint inEndpoint) {
    return new SimpleInConnector(
        inEndpoint.getUri(),
        inEndpoint.getId(),
        inEndpoint.getDomainClassType().orElse(null),
        inEndpoint.getTransformFunction().orElse(null));
  }

  @Override
  public void configure() {
    from(ep).to("log:messageIn").setBody(body().convertToString());
  }

  public String getName() {
    return name;
  }
}
