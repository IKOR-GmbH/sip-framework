package de.ikor.sip.foundation.core.declarative.validator;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {

  public static final String TO_CDM_EXCEPTION_MESSAGE =
      "Missing data transformation in connector %s. Body type was %s, but when sending to integration scenario %s, body type should be %s";

  public static final String FROM_CDM_EXCEPTION_MESSAGE =
      "Missing data transformation in connector %s. Body type was %s, but when receiving from integration scenario %s, body type should be %s";

  private final String scenario;

  private final String connector;
  private final Class<?> centralDomainModel;

  private final String exceptionMessage;

  public CDMValidator(
      String scenario, String connector, Class<?> centralDomainModel, String exceptionMessage) {
    this.scenario = scenario;
    this.connector = connector;
    this.centralDomainModel = centralDomainModel;
    this.exceptionMessage = exceptionMessage;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    if (!centralDomainModel.isInstance(exchange.getMessage().getBody())) {
      throw SIPFrameworkException.initException(
          exceptionMessage,
          connector,
          exchange.getMessage().getBody().getClass().getName(),
          scenario,
          centralDomainModel.getName());
    }
  }
}
