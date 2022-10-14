package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.exceptions;

/** Exception for impossible invoking first processor in tested route */
public class RouteInvokerRuntimeException extends RuntimeException {

  public RouteInvokerRuntimeException(String routeInvokerName) {
    super(String.format("%s could not invoke route under test", routeInvokerName));
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
