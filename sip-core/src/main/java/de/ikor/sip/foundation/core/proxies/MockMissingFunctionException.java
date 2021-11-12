package de.ikor.sip.foundation.core.proxies;

/** Exception for missing mock function in {@link ProcessorProxy} */
public class MockMissingFunctionException extends RuntimeException {

  /**
   * Creates new instance of MockMissingFunctionException
   *
   * @param message message to be shown
   */
  public MockMissingFunctionException(String message) {
    super(message);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
