package de.ikor.sip.foundation.core.actuator.common;

/** The custom runtime exception to be used as a default exception in this library. */
public class IntegrationManagementException extends RuntimeException {

  /**
   * Constructs an IntegrationManagementException.
   *
   * @param message Exception message
   * @param cause Throwable
   */
  public IntegrationManagementException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs an IntegrationManagementException.
   *
   * @param message Exception message
   */
  public IntegrationManagementException(String message) {
    super(message);
  }
}
