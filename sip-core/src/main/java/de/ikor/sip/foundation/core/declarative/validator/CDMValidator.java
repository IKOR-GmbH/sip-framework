package de.ikor.sip.foundation.core.declarative.validator;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {
  private final Class<?> centralModelRequest;

  public CDMValidator(Class<?> centralModelRequest) {
    this.centralModelRequest = centralModelRequest;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    if (!centralModelRequest.isInstance(exchange.getMessage().getBody())) {
      throw new SIPFrameworkException(
          String.format(
              "Wrong data type. Expected: %s, but was:  %s",
              centralModelRequest.getName(), exchange.getMessage().getBody().getClass().getName()));
    }
  }
}
