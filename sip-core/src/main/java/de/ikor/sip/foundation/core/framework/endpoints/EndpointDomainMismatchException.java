package de.ikor.sip.foundation.core.framework.endpoints;

/**
 * Exception thrown when there is a domain mismatch in {@link InEndpoint} or {@link OutEndpoint}.
 */
public class EndpointDomainMismatchException extends RuntimeException {

  public EndpointDomainMismatchException(String endpointUrl) {
    super(String.format("There is a domain mismatch for endpoint %s", endpointUrl));
  }

  /**
   * Method for hiding stack trace in console
   *
   * @return Throwable
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
