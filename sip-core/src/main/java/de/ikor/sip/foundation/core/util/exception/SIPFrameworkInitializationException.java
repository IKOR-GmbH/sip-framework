package de.ikor.sip.foundation.core.util.exception;

import lombok.experimental.StandardException;

/** Exception class for exception that are thrown by the framework during initialization phase */
@StandardException
public class SIPFrameworkInitializationException extends SIPFrameworkException {

  public static SIPFrameworkInitializationException initException(
      String messagePattern, Object... args) {
    return new SIPFrameworkInitializationException(String.format(messagePattern, args));
  }

  public static SIPFrameworkInitializationException initException(
      Throwable cause, String messagePattern, Object... args) {
    return new SIPFrameworkInitializationException(String.format(messagePattern, args), cause);
  }
}
