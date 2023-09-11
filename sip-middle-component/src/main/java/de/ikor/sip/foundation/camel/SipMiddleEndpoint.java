package de.ikor.sip.foundation.camel;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * {@link SipMiddleEndpoint} is a decorator over the targetEndpoint that provides additional routing
 * logic to existing Camel routes.
 */
@UriEndpoint(
    firstVersion = "1.0-SNAPSHOT",
    scheme = "sipmc",
    title = "sipmc",
    syntax = "sipmc:name",
    category = {Category.CORE})
public final class SipMiddleEndpoint extends DefaultEndpoint {
  /**
   * The endpointAlias is the path parameter from the endpoint URI. While unused here, this variable
   * is still necessary for the code generation from camel-maven-plugin
   */
  @UriPath
  @Metadata(required = true)
  private final String uri;

  private final Endpoint targetEndpoint;

  /**
   * Creates new instance of {@link SipMiddleEndpoint}
   *
   * @param uri endpoint uri
   * @param component {@link SipMiddleComponent}
   * @param targetEndpointUri the uri of the target endpoint
   */
  public SipMiddleEndpoint(String uri, SipMiddleComponent component, String targetEndpointUri) {
    super(uri, component);
    this.uri = uri;
    targetEndpoint = getCamelContext().getEndpoint(targetEndpointUri);
  }

  @Override
  public Producer createProducer() throws Exception {
    return new SipMiddleProducer(targetEndpoint);
  }

  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    return targetEndpoint.createConsumer(processor);
  }
}
