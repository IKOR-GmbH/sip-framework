package de.ikor.sip.foundation.core.actuator.routes;

/**
 * Exception thrown when operation is incompatible to ones in {@link
 * de.ikor.sip.foundation.core.actuator.routes.RouteOperation}
 */
public class IncompatibleOperationException extends RuntimeException {

  /**
   * Creates new instance of IncompatibleOperationException
   *
   * @param message Exception message
   */
  public IncompatibleOperationException(String message) {
    super(message);
  }
}
