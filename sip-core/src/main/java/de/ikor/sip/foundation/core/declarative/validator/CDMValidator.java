package de.ikor.sip.foundation.core.declarative.validator;

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
      throw new IllegalStateException(
          "Wrong data type. Expected: "
              + centralModelRequest.getName()
              + ", but was: "
              + exchange.getMessage().getBody().getClass().getName());
    }
  }
}
