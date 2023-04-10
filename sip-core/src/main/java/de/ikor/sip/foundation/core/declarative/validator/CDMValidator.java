package de.ikor.sip.foundation.core.declarative.validator;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {

  private final String scenario;

  private final String connector;
  private final Class<?> centralDomainModel;

  public CDMValidator(String scenario, String connector, Class<?> centralDomainModel) {
    this.scenario = scenario;
    this.connector = connector;
    this.centralDomainModel = centralDomainModel;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    if (!centralDomainModel.isInstance(exchange.getMessage().getBody())) {
      throw new SIPFrameworkException(
          String.format(
              "Wrong data type in connector %s. Body type was %s, but when sending to integration scenario %s, body type should be %s",
              connector,
              exchange.getMessage().getBody().getClass().getName(),
              scenario,
              centralDomainModel.getName()));
    }
  }
}
