package de.ikor.sip.testframework.util;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

/** Util class that executes a request to a certain route (defined in the Exchange) */
@Component
@RequiredArgsConstructor
public class SIPRouteProducerTemplate {
  private final ProducerTemplate producerTemplate;
  private final SIPEndpointResolver sipEndpointResolver;

  /**
   * Request an exchange on camel route
   *
   * @param exchange {@link Exchange} that is sent to a route
   * @return {@link Exchange} result of request
   */
  public Exchange requestOnRoute(Exchange exchange) {
    String endpointURI = sipEndpointResolver.resolveURI(exchange);
    return producerTemplate.send(endpointURI, exchange);
  }
}
