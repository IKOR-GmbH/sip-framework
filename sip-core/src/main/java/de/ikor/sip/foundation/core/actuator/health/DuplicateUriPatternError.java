package de.ikor.sip.foundation.core.actuator.health;

/**
 * Exception duplicated uri patterns (health indicator matchers) used in {@link
 * EndpointHealthRegistry}
 */
public class DuplicateUriPatternError extends RuntimeException {

  /**
   * Creates new instance of DuplicateUriPatternError
   *
   * @param message message to be shown
   */
  public DuplicateUriPatternError(String message) {
    super(message);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
