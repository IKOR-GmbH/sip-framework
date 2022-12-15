package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@AllArgsConstructor
public class EndpointDomainValidation implements Processor {

  private Class<?> domainClassType;
  private String endpointUri;

  @Override
  public void process(Exchange exchange) throws Exception {
    if (!domainClassType.isAssignableFrom(exchange.getMessage().getBody().getClass())) {
      throw new EndpointDomainMismatchException(endpointUri);
    }
  }
}
