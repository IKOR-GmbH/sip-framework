package de.ikor.sip.foundation.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultProducer;

/**
 * {@link SipMiddleProducer} is a decorator over the producer of the target endpoint from the {@link
 * SipMiddleEndpoint}.
 */
@Slf4j
public class SipMiddleProducer extends DefaultProducer {
  private final Producer producer;

  /**
   * Creates new instance of SipPlatformProducer
   *
   * @param endpoint {@link SipMiddleEndpoint}
   * @throws Exception if the the wrapped producer could not be created from the given endpoint
   */
  public SipMiddleProducer(Endpoint endpoint) throws Exception {
    super(endpoint);
    this.producer = endpoint.createProducer();
  }

  @Override
  protected void doStart() throws Exception {
    super.doStart();
    producer.start();
  }

  @Override
  protected void doStop() throws Exception {
    super.doStop();
    producer.stop();
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    log.debug("sip.mc.processexchange_{}", exchange);
    // additional logic
    producer.process(exchange);
  }
}
