package de.ikor.sip.foundation.core.util.exception;

/** Base exception class for exception that are thrown by the adapter */
public class SIPAdapterException extends RuntimeException {
  public SIPAdapterException() {}

  public SIPAdapterException(String message) {
    super(message);
  }

  public SIPAdapterException(String message, Throwable cause) {
    super(message, cause);
  }

  public SIPAdapterException(Throwable cause) {
    super(cause);
  }
}