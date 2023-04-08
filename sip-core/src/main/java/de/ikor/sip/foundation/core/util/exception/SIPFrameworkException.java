package de.ikor.sip.foundation.core.util.exception;

import lombok.experimental.StandardException;

/** Base exception class for exception that are thrown by the framework */
@StandardException
public class SIPFrameworkException extends RuntimeException {

  // Suppressing stack trace for SIP Exception, if there is a cause passed
  // to the exception, it's stack trace will still be shown.
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
