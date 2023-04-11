package de.ikor.sip.foundation.core.util.exception;

import lombok.experimental.StandardException;

/** Exception class for exception that are thrown by the framework during initialization phase */
@StandardException
public class SIPFrameworkInitializationException extends SIPFrameworkException {

  /**
   * Static method for creating exception with provided message pattern and message arguments.
   *
   * @param messagePattern exception message in form of a string patter
   * @param args arguments for message pattern
   * @return initialized SIPFrameworkInitializationException
   */
  public static SIPFrameworkInitializationException initException(
      String messagePattern, Object... args) {
    return new SIPFrameworkInitializationException(String.format(messagePattern, args));
  }

  /**
   * Static method for creating exception with provided message pattern and message arguments.
   *
   * @param cause exception cause
   * @param messagePattern exception message in form of a string patter
   * @param args arguments for message pattern
   * @return initialized SIPFrameworkInitializationException
   */
  public static SIPFrameworkInitializationException initException(
      Throwable cause, String messagePattern, Object... args) {
    return new SIPFrameworkInitializationException(String.format(messagePattern, args), cause);
  }
}
