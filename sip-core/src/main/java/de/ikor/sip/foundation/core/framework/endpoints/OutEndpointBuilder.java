package de.ikor.sip.foundation.core.framework.endpoints;

import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Expression;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.builder.EndpointProducerBuilder;

public class OutEndpointBuilder implements EndpointProducerBuilder {
  private final String uri;

  public static OutEndpointBuilder outEndpointBuilder(String uri, String s1) {
    return new OutEndpointBuilder(uri);
  }

  private OutEndpointBuilder(String uri) {
    this.uri = uri;
  }

  @Override
  public String getUri() {
    return uri;
  }

  @Override
  public String getRawUri() {
    return uri;
  }

  @Override
  public void doSetProperty(String name, Object value) {}

  @Override
  public void doSetMultiValueProperty(String name, String key, Object value) {}

  @Override
  public void doSetMultiValueProperties(String name, String prefix, Map<String, Object> values) {}

  @Override
  public Expression expr(CamelContext camelContext) {
    return null;
  }

  @Override
  public Endpoint resolve(CamelContext context) throws NoSuchEndpointException {
    return null;
  }

  @Override
  public <T extends Endpoint> T resolve(CamelContext context, Class<T> endpointType)
      throws NoSuchEndpointException {
    return null;
  }
}
