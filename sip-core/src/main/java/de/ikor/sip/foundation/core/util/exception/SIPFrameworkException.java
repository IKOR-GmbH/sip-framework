package de.ikor.sip.foundation.core.util.exception;

/** Base exception class for exception that are thrown by the framework */
public class SIPFrameworkException extends RuntimeException {
  public SIPFrameworkException() {}

  public SIPFrameworkException(String message) {
    super(message);
  }

  public SIPFrameworkException(String message, Throwable cause) {
    super(message, cause);
  }

  public SIPFrameworkException(Throwable cause) {
    super(cause);
  }
}
