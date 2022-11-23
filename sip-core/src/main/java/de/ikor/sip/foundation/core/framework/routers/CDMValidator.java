package de.ikor.sip.foundation.core.framework.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {
  private Class<?> centralModelRequest;

  public CDMValidator(Class<?> centralModelRequest) {
    this.centralModelRequest = centralModelRequest;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    //TODO fix it
    if (centralModelRequest.isInstance(CentralRouterDomainModel.undefined)
        && exchange.getMessage().getBody() != null) {
      throw new IllegalStateException("Wrong data type. Expected: no body present");
    }
    if (!this.centralModelRequest.getName().equals(exchange.getMessage().getBody().getClass().getName())) {
      throw new IllegalStateException(
          "Wrong data type. Expected: " + centralModelRequest.getName());
    }
  }
}
