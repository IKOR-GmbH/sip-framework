package de.ikor.sip.foundation.core.actuator.common;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;

/** The custom runtime exception to be used as a default exception in this library. */
public class IntegrationManagementException extends SIPFrameworkException {

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
