package de.ikor.sip.foundation.core.declarative.validator;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CDMValidator implements Processor {

  public static final String REQUEST_EXCEPTION_MESSAGE =
      "Wrong data type in exchange body when sending request to central domain. Body type was %s, but type should be same as request model in integration scenario: %s";

  public static final String RESPONSE_EXCEPTION_MESSAGE =
      "Wrong data type in exchange body when sending response to central domain. Body type was %s, but type should be same as response model in integration scenario: %s";

  private final Class<?> centralDomainModel;

  private final String message;

  public CDMValidator(Class<?> centralDomainModel, String message) {
    this.centralDomainModel = centralDomainModel;
    this.message = message;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    if (!centralDomainModel.isInstance(exchange.getMessage().getBody())) {
      throw new SIPFrameworkException(
          String.format(
              message,
              exchange.getMessage().getBody().getClass().getName(),
              centralDomainModel.getName()));
    }
  }
}
