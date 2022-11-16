package de.ikor.sip.foundation.core.framework.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {
  private Class<?> centralModelRequest;

  public CDMValidator(Class<?> centralModelRequest) {
    this.centralModelRequest = centralModelRequest;
  }

  public CDMValidator(String fullClassName) {
    try {
      this.centralModelRequest = Class.forName(fullClassName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e); // TODO
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    if (centralModelRequest.isInstance(CentralRouterDomainModel.undefined)
        && exchange.getMessage().getBody() != null) {
      throw new IllegalStateException("Wrong data type. Expected: no body present");
    }
    if (!centralModelRequest.isInstance(exchange.getMessage().getBody())) {
      throw new IllegalStateException(
          "Wrong data type. Expected: " + centralModelRequest.getName());
    }
  }
}
